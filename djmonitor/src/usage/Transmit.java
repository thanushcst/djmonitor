package usage;

import java.io.Serializable;

public class Transmit implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8281747016988077029L;
	/**
	 * Transmited
	 */
	private long TX_Bytes;
	private int TX_CarrierErrors;
	private int TX_Collisions;
	private int TX_Compressed;
	private int TX_Dropped;
	private int TX_Erros;
	private int TX_Fifo;
	private int TX_Packets;

	/**
	 * @param tXBytes
	 * @param tXCarrierErrors
	 * @param tXCollisions
	 * @param tXCompressed
	 * @param tXDropped
	 * @param tXErros
	 * @param tXFifo
	 * @param tXPackets
	 */
	public Transmit(long tXBytes,
            int tXPackets,
            int tXErros,
            int tXDropped,
            int tXFifo,
            int tXCollisions,
            int tXCarrierErrors,
            int tXCompressed) {
		TX_Bytes = tXBytes;
		TX_CarrierErrors = tXCarrierErrors;
		TX_Collisions = tXCollisions;
		TX_Compressed = tXCompressed;
		TX_Dropped = tXDropped;
		TX_Erros = tXErros;
		TX_Fifo = tXFifo;
		TX_Packets = tXPackets;
	}



	/**
	 * @return the tX_Bytes
	 */
	public long getTX_Bytes() {
		return TX_Bytes;
	}



	/**
	 * @return the tX_CarrierErrors
	 */
	public int getTX_CarrierErrors() {
		return TX_CarrierErrors;
	}



	/**
	 * @return the tX_Collisions
	 */
	public int getTX_Collisions() {
		return TX_Collisions;
	}



	/**
	 * @return the tX_Compressed
	 */
	public int getTX_Compressed() {
		return TX_Compressed;
	}



	/**
	 * @return the tX_Dropped
	 */
	public int getTX_Dropped() {
		return TX_Dropped;
	}



	/**
	 * @return the tX_Erros
	 */
	public int getTX_Erros() {
		return TX_Erros;
	}



	/**
	 * @return the tX_Fifo
	 */
	public int getTX_Fifo() {
		return TX_Fifo;
	}



	/**
	 * @return the tX_Packets
	 */
	public int getTX_Packets() {
		return TX_Packets;
	}

	
}