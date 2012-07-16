package usage;

import java.io.Serializable;

public class Receive implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -867141219197414115L;
	/**
	 * Received
	 */
	private long RX_Bytes;
	private int RX_Compressed;
	private int RX_Dropped;
	private int RX_Erros;
	private int RX_Fifo;
	private int RX_Frame;
	private int RX_Multicast;
	private int RX_Packets;

	/**
	 * @param rXBytes
	 * @param rXCompressed
	 * @param rXDropped
	 * @param rXErros
	 * @param rXFifo
	 * @param rXFrame
	 * @param rXMulticast
	 * @param rXPackets
	 */
	public Receive(
                long rXBytes,
            int rXPackets,
            int rXErros,
            int rXDropped,
            int rXFifo,
            int rXFrame,
            int rXCompressed,
            int rXMulticast) {
		RX_Bytes = rXBytes;
		RX_Compressed = rXCompressed;
		RX_Dropped = rXDropped;
		RX_Erros = rXErros;
		RX_Fifo = rXFifo;
		RX_Frame = rXFrame;
		RX_Multicast = rXMulticast;
		RX_Packets = rXPackets;
	}

	/**
	 * @return the rX_Bytes
	 */
	public long getRX_Bytes() {
		return RX_Bytes;
	}

	/**
	 * @return the rX_Compressed
	 */
	public int getRX_Compressed() {
		return RX_Compressed;
	}

	/**
	 * @return the rX_Dropped
	 */
	public int getRX_Dropped() {
		return RX_Dropped;
	}

	/**
	 * @return the rX_Erros
	 */
	public int getRX_Erros() {
		return RX_Erros;
	}

	/**
	 * @return the rX_Fifo
	 */
	public int getRX_Fifo() {
		return RX_Fifo;
	}

	/**
	 * @return the rX_Frame
	 */
	public int getRX_Frame() {
		return RX_Frame;
	}

	/**
	 * @return the rX_Multicast
	 */
	public int getRX_Multicast() {
		return RX_Multicast;
	}

	/**
	 * @return the rX_Packets
	 */
	public int getRX_Packets() {
		return RX_Packets;
	}

}