package storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import usage.MonitoredData;

public class HistoricalDatabase
{

	private Connection conn;
	private Statement stm;
	private DateTime date;

	/**
	 * O construtor cria uma nova conexão com o banco de dados sqlite contido no
	 * arquivo passado como parâmetro. A conexão é possibilitada pelo driver
	 * JDBC, fornecido por SQLiteJDBC.
	 */
	public HistoricalDatabase(String file)
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			date = new DateTime();
			conn = DriverManager.getConnection("jdbc:sqlite:" + file);

			initDB();
		} catch (final SQLException ex)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, null, ex);
		} catch (final ClassNotFoundException ex)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	/**
	 * Cria uma nova tabela de recordes, apagando tudo que houvesse na base
	 * anteriormente.
	 */
	private void initDB()
	{
		try
		{
			stm = conn.createStatement();
			final String[] create_tables = CREATE_TABLES.split(";");
			for (final String query : create_tables)
				stm.executeUpdate(query);

		} catch (final SQLException ex)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	/**
	 * Adiciona uma nova linha na tabela de recordes.
	 */
	public boolean saveOrUpdate(MonitoredData mData)
	{
		final String timeID = getTimeLastRowID();

		// statements prepared and executed here
		// Insere na tabela de nodos este cliente, usando como primary key o
		// hashCode do IP address da máquina
		saveOrUpdateToDatabase(INSERT_NODE.replace("?",
				String.valueOf(mData.getNodeID())));

		saveOrUpdateToDatabase(String.format(INSERT_MEMORY_USAGE, mData
				.getNodeID(), timeID, mData.getMem().getSize(), mData.getMem()
				.getResident(), mData.getMem().getShare(), mData.getMem()
				.getText(), mData.getMem().getData(),
				mData.getMem().getVsize(), mData.getMem().getRss(), mData
						.getMem().getRsslim(), mData.getMem().getMemTotal(),
				mData.getMem().getMemUsed(), mData.getMem().getMemFree(), mData
						.getMem().getMemBuffers(), mData.getMem()
						.getMemCached()));
		for (int core = 0; core < mData.getCpu().keySet().size(); core++)
			saveOrUpdateToDatabase(String.format(INSERT_CPU_USAGE,
					mData.getNodeID(), timeID, mData.getCpu().get(core)
							.getCoreId(), mData.getCpu().get(core).getUser(),
					mData.getCpu().get(core).getNice(), mData.getCpu()
							.get(core).getSysmode(), mData.getCpu().get(core)
							.getIdle(), mData.getCpu().get(core).getIowait(),
					mData.getCpu().get(core).getIrq(), mData.getCpu().get(core)
							.getSoftirq(), mData.getCpu().get(core).getSteal(),
					mData.getCpu().get(core).getGuest()));
		for (final String o : mData.getDisk().keySet())
			saveOrUpdateToDatabase(String.format(INSERT_DISK_USAGE,
					mData.getNodeID(), timeID,
					mData.getDisk().get(o).getName(), mData.getDisk().get(o)
							.getReadsCompleted(), mData.getDisk().get(o)
							.getReadsMerged(), mData.getDisk().get(o)
							.getWritesMerged(), mData.getDisk().get(o)
							.getSectorsRead(), mData.getDisk().get(o)
							.getMillisecondsReading(), mData.getDisk().get(o)
							.getWritesCompleted(), mData.getDisk().get(o)
							.getSectorsWritten(), mData.getDisk().get(o)
							.getMillisecondsWriting(), mData.getDisk().get(o)
							.getIosInProgress(), mData.getDisk().get(o)
							.getMillisecondsSpentInIO(), mData.getDisk().get(o)
							.getWeightedMillisecondsDoingIO()));
		for (final String o : mData.getNet().keySet())
			saveOrUpdateToDatabase(String.format(INSERT_NETWORK_USAGE,
					mData.getNodeID(), timeID, mData.getNet().get(o)
							.getInterfaceName(), mData.getNet().get(o)
							.getReceive().getRX_Bytes(), mData.getNet().get(o)
							.getReceive().getRX_Packets(), mData.getNet()
							.get(o).getReceive().getRX_Erros(), mData.getNet()
							.get(o).getReceive().getRX_Dropped(), mData
							.getNet().get(o).getReceive().getRX_Fifo(), mData
							.getNet().get(o).getReceive().getRX_Frame(), mData
							.getNet().get(o).getReceive().getRX_Compressed(),
					mData.getNet().get(o).getReceive().getRX_Multicast(), mData
							.getNet().get(o).getTransmit().getTX_Bytes(), mData
							.getNet().get(o).getTransmit().getTX_Packets(),
					mData.getNet().get(o).getTransmit().getTX_Erros(), mData
							.getNet().get(o).getTransmit().getTX_Dropped(),
					mData.getNet().get(o).getTransmit().getTX_Fifo(), mData
							.getNet().get(o).getTransmit().getTX_Collisions(),
					mData.getNet().get(o).getTransmit().getTX_CarrierErrors(),
					mData.getNet().get(o).getTransmit().getTX_Compressed()));

		return true;
	}

	/**
	 * Adiciona uma nova linha na tabela e retorna o ID da linha inserida
	 * 
	 * @param insert
	 *            : the insert statement
	 * @return o ID da linha inserida
	 */
	private synchronized int saveOrUpdateToDatabase(String insert)
	{
		try
		{
			stm = conn.createStatement();
			stm.executeUpdate(insert);
			return getLastInsertRowId();
		} catch (final SQLException ex)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return -1;
	}

	/**
	 * @return The ID of the last row inserted in the database
	 * @throws SQLException
	 */
	private int getLastInsertRowId()
	{
		int rowID = 0;
		ResultSet rs;
		try
		{
			rs = stm.getGeneratedKeys();
			while (rs.next())
				rowID = rs.getInt(1);
		} catch (final SQLException ex)
		{
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return rowID;
	}

	/**
	 * Insert a time row in the time table and return the last row id generated.
	 */
	private String getTimeLastRowID()
	{
		return String.valueOf(saveOrUpdateToDatabase(String.format(INSERT_TIME,
				getTime())));
	}

	/**
	 * Get the current time in the following format: SECOND,MINUTE,HOUR,Month
	 * DAY,MONTH,YEAR,Week DAY,Year DAY, is Daylight Savings Time
	 */
	private String getTime()
	{
		return String
				.format("%d, %d, %d, %d, %d, %d, %d, %d, %d", date
						.getSecondOfMinute(), date.getMinuteOfHour(), date
						.getHourOfDay(), date.getDayOfMonth(), date
						.getMonthOfYear(), date.getYear(), date.getDayOfWeek(),
						date.getDayOfYear(), (TimeZone.getDefault()
								.inDaylightTime(new Date()) == true ? 1 : 0));
	}

	/**
	 * Insert into TIME table
	 */
	private static final String INSERT_TIME = "INSERT INTO Time (SECOND,MINUTE,HOUR,M_DAY,MONTH,YEAR,W_DAY,Y_DAY,IS_DST) VALUES (%s); ";
	/**
	 * Insert into NODE table
	 */
	private static final String INSERT_NODE = "INSERT OR REPLACE INTO Node (NODE_ID) VALUES (?);";
	/**
	 * Insert into CPU table
	 */
	private static final String INSERT_CPU_USAGE = "INSERT INTO CpuUsage (NODE_ID, TIME_ID, CORE_ID, USER, NICE, SYSMODE, IDLE, IOWAIT, IRQ, SOFTIRQ, STEAL, GUEST) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";
	/**
	 * Insert into Memory table
	 */
	private static final String INSERT_MEMORY_USAGE = "INSERT INTO MemoryUsage (NODE_ID, TIME_ID, SIZE, RESIDENT, SHARE, TEXT, DATA, VIRTUALSIZE, RSS, RSSLIM, MEM_TOTAL, MEM_USED, MEM_FREE, MEM_BUFFERS, MEM_CACHED) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";
	/**
	 * Insert into Network table
	 */
	private static final String INSERT_NETWORK_USAGE = "INSERT INTO NetworkUsage (NODE_ID, TIME_ID, INTERFACE, R_BYTES, R_PACKETS, R_ERRORS, R_DROP, R_FIFO, R_FRAME, R_COMPRESSED, R_MULTICAST, T_BYTES, T_PACKETS, T_ERRORS, T_DROP, T_FIFO, T_COLLS, T_CARRIER, T_COMPRESSED) VALUES (%s, %s, '%s', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";
	/**
	 * Insert into Disk table
	 */
	private static final String INSERT_DISK_USAGE = "INSERT INTO DiskUsage (NODE_ID, TIME_ID, PARTITION_NAME, READS_COMPLETED, READS_MERGED, WRITES_MERGED, SECTORS_READ, MILLISECONDS_READING, WRITES_COMPLETED, SECTORS_WRITTEN, MILLISECONDS_WRITING, IO_IN_PROGRESS, MILLISECONDS_SPENT_IN_IO, WEIGHTED_MILLISECONDS_DOING_IO) VALUES (%s, %s, '%s', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";
	/**
	 * Create table
	 */
	private static final String CREATE_TABLES = "CREATE TABLE IF NOT EXISTS Time(ID INTEGER PRIMARY KEY, SECOND INTEGER NOT NULL, MINUTE INTEGER NOT NULL, HOUR  INTEGER NOT NULL, M_DAY INTEGER NOT NULL, MONTH INTEGER NOT NULL, YEAR  INTEGER NOT NULL, W_DAY INTEGER NOT NULL, Y_DAY INTEGER NOT NULL, IS_DST INTEGER NOT NULL);" + "CREATE TABLE IF NOT EXISTS Node(ID INTEGER PRIMARY KEY, NODE_ID INTEGER UNIQUE NOT NULL);" + "CREATE TABLE IF NOT EXISTS CpuUsage(ID INTEGER, NODE_ID INTEGER NOT NULL REFERENCES Node ( NODE_ID ) ON DELETE CASCADE ON UPDATE CASCADE, TIME_ID INTEGER NOT NULL REFERENCES Time( ID )ON DELETE CASCADE ON UPDATE CASCADE, CORE_ID INTEGER NOT NULL, USER INTEGER, NICE INTEGER, SYSMODE INTEGER, IDLE INTEGER, IOWAIT INTEGER, IRQ INTEGER, SOFTIRQ INTEGER, STEAL  INTEGER, GUEST INTEGER, PRIMARY KEY ( ID, NODE_ID, TIME_ID, CORE_ID ), FOREIGN KEY ( TIME_ID ) REFERENCES Time ( ID ) ON DELETE CASCADE ON UPDATE CASCADE);" + "CREATE TABLE IF NOT EXISTS DiskUsage(ID INTEGER, NODE_ID INTEGER NOT NULL REFERENCES Node ( NODE_ID ) ON DELETE CASCADE ON UPDATE CASCADE, TIME_ID INTEGER NOT NULL REFERENCES Time ( ID ) ON DELETE CASCADE ON UPDATE CASCADE, PARTITION_NAME TEXT, READS_COMPLETED INTEGER, READS_MERGED INTEGER, WRITES_MERGED INTEGER, SECTORS_READ INTEGER, MILLISECONDS_READING INTEGER, WRITES_COMPLETED INTEGER, SECTORS_WRITTEN INTEGER, MILLISECONDS_WRITING INTEGER, IO_IN_PROGRESS INTEGER, MILLISECONDS_SPENT_IN_IO INTEGER, WEIGHTED_MILLISECONDS_DOING_IO INTEGER, PRIMARY KEY ( ID, NODE_ID, TIME_ID ), FOREIGN KEY ( NODE_ID ) REFERENCES Node ( NODE_ID ) ON DELETE CASCADE ON UPDATE CASCADE); " + "CREATE TABLE IF NOT EXISTS MemoryUsage(ID INTEGER, NODE_ID INTEGER NOT NULL REFERENCES Node ( NODE_ID )ON DELETE CASCADE ON UPDATE CASCADE, TIME_ID INTEGER NOT NULL REFERENCES Time ( ID ) ON DELETE CASCADE ON UPDATE CASCADE, SIZE INTEGER, RESIDENT INTEGER, SHARE INTEGER, TEXT INTEGER, DATA INTEGER, VIRTUALSIZE INTEGER, RSS INTEGER, RSSLIM TEXT, MEM_TOTAL  INTEGER, MEM_USED INTEGER, MEM_FREE INTEGER, MEM_BUFFERS INTEGER, MEM_CACHED INTEGER, PRIMARY KEY ( ID, NODE_ID, TIME_ID ), FOREIGN KEY( NODE_ID ) REFERENCES Node ( NODE_ID ) ON DELETE CASCADE ON UPDATE CASCADE); " + "CREATE TABLE IF NOT EXISTS NetworkUsage(ID INTEGER, NODE_ID INTEGER NOT NULL REFERENCES Node( NODE_ID ) ON DELETE CASCADE ON UPDATE CASCADE, TIME_ID INTEGER NOT NULL REFERENCES Time ( ID ) ON DELETE CASCADE ON UPDATE CASCADE, INTERFACE TEXT, R_BYTES INTEGER, R_PACKETS  INTEGER, R_ERRORS INTEGER, R_DROP INTEGER, R_FIFO INTEGER, R_FRAME INTEGER, R_COMPRESSED INTEGER, R_MULTICAST INTEGER, T_BYTES INTEGER, T_PACKETS INTEGER, T_ERRORS INTEGER, T_DROP INTEGER, T_FIFO INTEGER, T_COLLS INTEGER, T_CARRIER INTEGER, T_COMPRESSED INTEGER, PRIMARY KEY ( ID, NODE_ID, TIME_ID ), FOREIGN KEY( NODE_ID ) REFERENCES Node ( NODE_ID ) ON DELETE CASCADE ON UPDATE CASCADE );";
	/**
	 * Create index
	 */
	@SuppressWarnings("unused")
	private static final String CREATE_INDEXES = "CREATE UNIQUE INDEX IF NOT EXISTS idx_CpuUsage ON CpuUsage (TIME_ID, NODE_ID, CORE_ID); " + "CREATE UNIQUE INDEX IF NOT EXISTS idx_DiskUsage ON DiskUsage (NODE_ID, TIME_ID); " + "CREATE UNIQUE INDEX IF NOT EXISTS idx_MemoryUsage ON MemoryUsage (NODE_ID, TIME_ID); " + "CREATE UNIQUE INDEX IF NOT EXISTS idx_NetworkUsage ON NetworkUsage (NODE_ID, TIME_ID, INTERFACE); " + "CREATE UNIQUE INDEX IF NOT EXISTS idx_Time ON Time (YEAR,W_DAY,Y_DAY,HOUR,M_DAY,MINUTE,SECOND,MONTH);";
}
