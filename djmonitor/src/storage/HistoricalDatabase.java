package storage;

import java.sql.*;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import usage.CpuData;
import usage.DiskData;
import usage.MonitoredData;
import usage.NetworkData;
import utils.Utils;

public class HistoricalDatabase {

	private Connection conn;
	private Statement stm;
	private DateTime date;
	private int nodeID;

	/**
	 * O construtor cria uma nova conexão com o banco de dados sqlite contido no
	 * arquivo passado como parâmetro. A conexão é possibilitada pelo driver
	 * JDBC, fornecido por SQLiteJDBC.
	 */
	public HistoricalDatabase(String file) {
		try {
			Class.forName("org.sqlite.JDBC");
			this.date = new DateTime();
			this.nodeID = Utils.getIPAddress();
			this.conn = DriverManager.getConnection("jdbc:sqlite:" + file);

			initDB();
		} catch (SQLException ex) {
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	/**
	 * Cria uma nova tabela de recordes, apagando tudo que houvesse na base
	 * anteriormente.
	 */
	private void initDB() {
		try {
			int result = -1;
			this.stm = this.conn.createStatement();
			String[] create_tables = CREATE_TABLES.split(";");
			for (String query : create_tables) {
				result = this.stm.executeUpdate(query);
			}

		} catch (SQLException ex) {
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	/**
	 * Adiciona uma nova linha na tabela de recordes.
	 */
	public boolean saveOrUpdate(MonitoredData mData) {
		String timeID = this.getTimeLastRowID();

		// statements prepared and executed here
		// Insere na tabela de nodos este cliente, usando como primary key o
		// hashCode do IP address da máquina
		saveOrUpdateToDatabase(INSERT_NODE.replace("?", String.valueOf(mData.getNodeID())));

		saveOrUpdateToDatabase(String.format(INSERT_MEMORY_USAGE, mData
				.getNodeID(), timeID, mData.getMem().getSize(), mData.getMem()
				.getResident(), mData.getMem().getShare(), mData.getMem()
				.getText(), mData.getMem().getData(),
				mData.getMem().getVsize(), mData.getMem().getRss(), mData
						.getMem().getRsslim(), mData.getMem().getMemTotal(),
				mData.getMem().getMemUsed(), mData.getMem().getMemFree(), mData
						.getMem().getMemBuffers(), mData.getMem()
						.getMemCached()));
		for (CpuData o : mData.getCpu()) {
			saveOrUpdateToDatabase(String.format(INSERT_CPU_USAGE, mData
					.getNodeID(), timeID, o.getCoreId(), o.getUser(), o
					.getNice(), o.getSysmode(), o.getIdle(), o.getIowait(), o
					.getIrq(), o.getSoftirq(), o.getSteal(), o.getGuest()));
		}
		for (DiskData o : mData.getDisk()) {
			saveOrUpdateToDatabase(String.format(INSERT_DISK_USAGE, mData
					.getNodeID(), timeID, o.getName(), o.getReadsCompleted(), o
					.getReadsMerged(), o.getWritesMerged(), o.getSectorsRead(),
					o.getMillisecondsReading(), o.getWritesCompleted(), o
							.getSectorsWritten(), o.getMillisecondsWriting(), o
							.getIosInProgress(), o.getMillisecondsSpentInIO(),
					o.getWeightedMillisecondsDoingIO()));
		}
		for (NetworkData o : mData.getNet()) {
			saveOrUpdateToDatabase(String.format(INSERT_NETWORK_USAGE, mData
					.getNodeID(), timeID, o.getInterfaceName(), o.getReceive()
					.getRX_Bytes(), o.getReceive().getRX_Packets(), o
					.getReceive().getRX_Erros(),
					o.getReceive().getRX_Dropped(),
					o.getReceive().getRX_Fifo(), o.getReceive().getRX_Frame(),
					o.getReceive().getRX_Compressed(), o.getReceive()
							.getRX_Multicast(), o.getTransmit().getTX_Bytes(),
					o.getTransmit().getTX_Packets(), o.getTransmit()
							.getTX_Erros(), o.getTransmit().getTX_Dropped(), o
							.getTransmit().getTX_Fifo(), o.getTransmit()
							.getTX_Collisions(), o.getTransmit()
							.getTX_CarrierErrors(), o.getTransmit()
							.getTX_Compressed()));
		}

		return true;
	}

	/**
	 * Adiciona uma nova linha na tabela e retorna o ID da linha inserida
	 * 
	 * @param insert
	 *            : the insert statement
	 * @return o ID da linha inserida
	 */
	private synchronized int saveOrUpdateToDatabase(String insert) {
		try {
			this.stm = this.conn.createStatement();
			this.stm.executeUpdate(insert);
			return getLastInsertRowId();
		} catch (SQLException ex) {
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return -1;
	}

	/**
	 * @return The ID of the last row inserted in the database
	 * @throws SQLException
	 */
	private int getLastInsertRowId() {
		int rowID = 0;
		ResultSet rs;
		try {
			rs = stm.getGeneratedKeys();
			while (rs.next()) {
				rowID = rs.getInt(1);
			}
		} catch (SQLException ex) {
			Logger.getLogger(HistoricalDatabase.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		return rowID;
	}

	/**
	 * Insert a time row in the time table and return the last row id generated.
	 */
	private String getTimeLastRowID() {
		return String.valueOf(saveOrUpdateToDatabase(String.format(INSERT_TIME,
				this.getTime())));
	}

	/**
	 * Get the current time in the following format: SECOND,MINUTE,HOUR,Month
	 * DAY,MONTH,YEAR,Week DAY,Year DAY, is Daylight Savings Time
	 */
	private String getTime() {
		return String.format("%d, %d, %d, %d, %d, %d, %d, %d, %d", this.date
				.getSecondOfMinute(), this.date.getMinuteOfHour(), this.date
				.getHourOfDay(), this.date.getDayOfMonth(), this.date
				.getMonthOfYear(), this.date.getYear(), this.date
				.getDayOfWeek(), this.date.getDayOfYear(), (TimeZone
				.getDefault().inDaylightTime(new Date()) == true ? 1 : 0));
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
	private static final String CREATE_TABLES = "CREATE TABLE IF NOT EXISTS Time(ID INTEGER PRIMARY KEY, SECOND INTEGER NOT NULL, MINUTE INTEGER NOT NULL, HOUR  INTEGER NOT NULL, M_DAY INTEGER NOT NULL, MONTH INTEGER NOT NULL, YEAR  INTEGER NOT NULL, W_DAY INTEGER NOT NULL, Y_DAY INTEGER NOT NULL, IS_DST INTEGER NOT NULL);"
			+ "CREATE TABLE IF NOT EXISTS Node(ID INTEGER PRIMARY KEY, NODE_ID INTEGER UNIQUE NOT NULL);"
			+ "CREATE TABLE IF NOT EXISTS CpuUsage(ID INTEGER, NODE_ID INTEGER NOT NULL REFERENCES Node ( NODE_ID ) ON DELETE CASCADE ON UPDATE CASCADE, TIME_ID INTEGER NOT NULL REFERENCES Time( ID )ON DELETE CASCADE ON UPDATE CASCADE, CORE_ID INTEGER NOT NULL, USER INTEGER, NICE INTEGER, SYSMODE INTEGER, IDLE INTEGER, IOWAIT INTEGER, IRQ INTEGER, SOFTIRQ INTEGER, STEAL  INTEGER, GUEST INTEGER, PRIMARY KEY ( ID, NODE_ID, TIME_ID, CORE_ID ), FOREIGN KEY ( TIME_ID ) REFERENCES Time ( ID ) ON DELETE CASCADE ON UPDATE CASCADE);"
			+ "CREATE TABLE IF NOT EXISTS DiskUsage(ID INTEGER, NODE_ID INTEGER NOT NULL REFERENCES Node ( NODE_ID ) ON DELETE CASCADE ON UPDATE CASCADE, TIME_ID INTEGER NOT NULL REFERENCES Time ( ID ) ON DELETE CASCADE ON UPDATE CASCADE, PARTITION_NAME TEXT, READS_COMPLETED INTEGER, READS_MERGED INTEGER, WRITES_MERGED INTEGER, SECTORS_READ INTEGER, MILLISECONDS_READING INTEGER, WRITES_COMPLETED INTEGER, SECTORS_WRITTEN INTEGER, MILLISECONDS_WRITING INTEGER, IO_IN_PROGRESS INTEGER, MILLISECONDS_SPENT_IN_IO INTEGER, WEIGHTED_MILLISECONDS_DOING_IO INTEGER, PRIMARY KEY ( ID, NODE_ID, TIME_ID ), FOREIGN KEY ( NODE_ID ) REFERENCES Node ( NODE_ID ) ON DELETE CASCADE ON UPDATE CASCADE); "
			+ "CREATE TABLE IF NOT EXISTS MemoryUsage(ID INTEGER, NODE_ID INTEGER NOT NULL REFERENCES Node ( NODE_ID )ON DELETE CASCADE ON UPDATE CASCADE, TIME_ID INTEGER NOT NULL REFERENCES Time ( ID ) ON DELETE CASCADE ON UPDATE CASCADE, SIZE INTEGER, RESIDENT INTEGER, SHARE INTEGER, TEXT INTEGER, DATA INTEGER, VIRTUALSIZE INTEGER, RSS INTEGER, RSSLIM TEXT, MEM_TOTAL  INTEGER, MEM_USED INTEGER, MEM_FREE INTEGER, MEM_BUFFERS INTEGER, MEM_CACHED INTEGER, PRIMARY KEY ( ID, NODE_ID, TIME_ID ), FOREIGN KEY( NODE_ID ) REFERENCES Node ( NODE_ID ) ON DELETE CASCADE ON UPDATE CASCADE); "
			+ "CREATE TABLE IF NOT EXISTS NetworkUsage(ID INTEGER, NODE_ID INTEGER NOT NULL REFERENCES Node( NODE_ID ) ON DELETE CASCADE ON UPDATE CASCADE, TIME_ID INTEGER NOT NULL REFERENCES Time ( ID ) ON DELETE CASCADE ON UPDATE CASCADE, INTERFACE TEXT, R_BYTES INTEGER, R_PACKETS  INTEGER, R_ERRORS INTEGER, R_DROP INTEGER, R_FIFO INTEGER, R_FRAME INTEGER, R_COMPRESSED INTEGER, R_MULTICAST INTEGER, T_BYTES INTEGER, T_PACKETS INTEGER, T_ERRORS INTEGER, T_DROP INTEGER, T_FIFO INTEGER, T_COLLS INTEGER, T_CARRIER INTEGER, T_COMPRESSED INTEGER, PRIMARY KEY ( ID, NODE_ID, TIME_ID ), FOREIGN KEY( NODE_ID ) REFERENCES Node ( NODE_ID ) ON DELETE CASCADE ON UPDATE CASCADE );";
	/**
	 * Create index
	 */
	private static final String CREATE_INDEXES = "CREATE UNIQUE INDEX IF NOT EXISTS idx_CpuUsage ON CpuUsage (TIME_ID, NODE_ID, CORE_ID); "
			+ "CREATE UNIQUE INDEX IF NOT EXISTS idx_DiskUsage ON DiskUsage (NODE_ID, TIME_ID); "
			+ "CREATE UNIQUE INDEX IF NOT EXISTS idx_MemoryUsage ON MemoryUsage (NODE_ID, TIME_ID); "
			+ "CREATE UNIQUE INDEX IF NOT EXISTS idx_NetworkUsage ON NetworkUsage (NODE_ID, TIME_ID, INTERFACE); "
			+ "CREATE UNIQUE INDEX IF NOT EXISTS idx_Time ON Time (YEAR,W_DAY,Y_DAY,HOUR,M_DAY,MINUTE,SECOND,MONTH);";
}