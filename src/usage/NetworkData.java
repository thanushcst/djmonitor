package usage;

import java.io.Serializable;

/**
 * @author pmdusso
 * @version 1.0 @created 24-abr-2012 15:22:27
 */
public class NetworkData implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4583951425149974664L;

	public NetworkData(String interfaceName,
            long rXBytes,
            int rXPackets,
            int rXErros,
            int rXDropped,
            int rXFifo,
            int rXFrame,
            int rXCompressed,
            int rXMulticast,
            long tXBytes,
            int tXPackets,
            int tXErros,
            int tXDropped,
            int tXFifo,
            int tXCollisions,
            int tXCarrierErrors,
            int tXCompressed) {

        InterfaceName = interfaceName;
        m_receive = new Receive(rXBytes, rXPackets, rXErros, rXDropped, rXFifo, rXFrame, rXCompressed, rXMulticast);
        m_transmit = new Transmit(tXBytes, tXPackets, tXErros, tXDropped, tXFifo, tXCollisions, tXCarrierErrors, tXCompressed);

    }
    private String InterfaceName;
    private Receive m_receive;
    private Transmit m_transmit;

    /**
     * INTERFACE, R_BYTES, R_PACKETS, R_ERRORS, R_DROP, R_FIFO, R_FRAME,
     * R_COMPRESSED, R_MULTICAST, T_BYTES, T_PACKETS, T_ERRORS, T_DROP, T_FIFO,
     * T_COLLS, T_CARRIER, T_COMPRESSED
     */
    @Override
    public String toString() {
        return "'" + InterfaceName + "'" + ", " + String.valueOf(this.m_receive.getRX_Bytes() + ", ")
                + String.valueOf(this.m_receive.getRX_Packets() + ", ")
                + String.valueOf(this.m_receive.getRX_Erros() + ", ")
                + String.valueOf(this.m_receive.getRX_Dropped() + ", ")
                + String.valueOf(this.m_receive.getRX_Fifo() + ", ")
                + String.valueOf(this.m_receive.getRX_Frame() + ", ")
                + String.valueOf(this.m_receive.getRX_Compressed() + ", ")
                + String.valueOf(this.m_receive.getRX_Multicast() + ", ")
                + String.valueOf(this.m_transmit.getTX_Bytes() + ", ")
                + String.valueOf(this.m_transmit.getTX_Packets() + ", ")
                + String.valueOf(this.m_transmit.getTX_Erros() + ", ")
                + String.valueOf(this.m_transmit.getTX_Dropped() + ", ")
                + String.valueOf(this.m_transmit.getTX_Fifo() + ", ")
                + String.valueOf(this.m_transmit.getTX_Collisions() + ", ")
                + String.valueOf(this.m_transmit.getTX_CarrierErrors() + ", ")
                + String.valueOf(this.m_transmit.getTX_Compressed());
    }

    /**
     * @return the interfaceName
     */
    public String getInterfaceName() {
        return InterfaceName;
    }

    /**
     * @return the m_receive
     */
    public Receive getReceive() {
        return m_receive;
    }

    /**
     * @return the m_transmit
     */
    public Transmit getTransmit() {
        return m_transmit;
    }
}// end NetworkData_t