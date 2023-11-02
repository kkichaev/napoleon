using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace DeviceEmu
{
    class Program
    {
        static readonly UInt32 CMD_PING = 0;

        static readonly UInt32 CMD_VNC_DATA = 1;
        static readonly UInt32 CMD_VNC_REQ = 2;
        static readonly UInt32 CMD_VNC_START = 4;
        static readonly UInt32 CMD_VNC_STOP = 5;

        static readonly UInt32 AVR_TEMP = 1090781205;
        static readonly UInt32 HUMIDITY_SENSOR1 = 2147745840;
        static readonly UInt32 ALARMTYPE = 1073807363;

        static uint USERID = 48731;
        static uint DEVICE = 112;

        //static string VNC_TEST = "192.168.0.138";
        static string VNC_TEST = "192.168.0.161";
        static int VNC_PORT = 5909;

       static string SERVER = "192.168.0.161";
        //static string SERVER = "control.egg-count.com";
        //static string pkt_str = "\x53\x4C\x4B\x47\x5B\xBE\x00\x00\x70\x00\x00\x00\x00\x00\x00\x00\x17\x00\x00\x00\x15\x00\x04\x41\x32\x32\x2E\x32\x00\x30\x00\x04\x80\x36\x36\x25\x00\x03\x00\x01\x40\x31\x00";

        static void Main(string[] args)
        {
            TcpClient cli = new TcpClient();
            try
            {

                cli.Connect(SERVER, 7654);
                NetworkStream str = cli.GetStream();
                str.ReadTimeout = 10 * 1000;

                while (true)
                {
                    byte[] packet = MakePingPacket(USERID, DEVICE);
                    str.Write(packet, 0, packet.Length);

                    int start = Environment.TickCount;
                    while (cli.Available == 0)
                    {
                        Thread.Sleep(5);
                        if(Environment.TickCount - start >= str.ReadTimeout)
                        {
                            break;
                        }
                    }

                    if (cli.Available > 0)
                    {
                        DevCommand dc = new DevCommand();
                        if( dc.Read(str) )
                        {
                            if(dc.cmd == CMD_VNC_REQ)
                            {
                                Console.WriteLine("Start VNC");
                                DoVncConnect(cli);
                            }
                        }
                    }
                }


            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
            cli.Close();

        }

        private static void DoVncConnect(TcpClient cli)
        {
            NetworkStream str = cli.GetStream();

            TcpClient vncCli = new TcpClient();
            bool wasConnect = false, exiting = false;
            NetworkStream vncStr = null;

            do
            {
                while (cli.Available == 0 && (!wasConnect || vncCli.Connected && vncCli.Available == 0))
                {
                    Thread.Sleep(5);
                }

                if (wasConnect && !vncCli.Connected)
                    break;

                byte[] rcv = null;
                while (vncCli.Available > 0)
                {
                    byte[] buffer = new byte[vncCli.Available];
                    int rc = vncStr.Read(buffer, 0, buffer.Length);
                    if (rc > 0)
                    {
                        if (rcv == null)
                            rcv = buffer;
                        else
                        {
                            byte[] z = new byte[rcv.Length + rc];
                            rcv.CopyTo(z, 0);
                            buffer.CopyTo(z, rcv.Length);
                            rcv = z;
                        }
                    }
                    Thread.Sleep(5);
                }
                if(rcv != null && rcv.Length > 0)
                {
                    //Console.WriteLine("Got from VNC  " + rcv.Length.ToString());
                    byte[] packet = MakeVNCPacket(USERID, DEVICE, rcv, rcv.Length);
                    str.Write(packet, 0, packet.Length);
                }
                while (cli.Available > 0)
                {
                    DevCommand dc = new DevCommand();
                    if (dc.Read(str))
                    {
                        if (dc.cmd == CMD_VNC_START)
                        {
                            Console.WriteLine("Open VNC");
                            vncCli.Connect(VNC_TEST, VNC_PORT);
                            vncStr = vncCli.GetStream();
                            wasConnect = true;
                        }
                        else if (dc.cmd == CMD_VNC_STOP)
                        {
                            Console.WriteLine("Stop VNC");
                            vncStr.Close();
                            vncCli.Close();
                            exiting = true;
                            break;
                        }
                        else if (dc.cmd == CMD_VNC_DATA)
                        {
                            //Console.WriteLine("Send to VNC " + dc.dataLen.ToString());
                            vncStr.Write(dc.data, 0, (int)dc.dataLen);
                        }
                    }
                }

            } while (!exiting);
        }

        static UInt32 countCRC(UInt32[] header)
        {
            byte[] packet = new byte[header.Length * 4];
            Buffer.BlockCopy(header, 0, packet, 0, header.Length * 4);

            UInt32 ret = 0;

            CRC crc = new CRC();

            for (int i = 4; i < header.Length - 4; i++)
                ret = crc.doCRC(header[i]);

            return ret;
        }

        private static byte[] MakeVNCPacket(UInt32 userid, UInt32 deviceid, byte[] data, int len)
        {
            Random rand = new Random();

            UInt32[] header = new UInt32[]
            {
                0x474B4C53,
                userid,
                deviceid,
                CMD_VNC_DATA,
                (UInt32)len,
                0,
            };

            header[5] = countCRC(header);

            byte[] packet = new byte[header.Length * 4 + len];
            Buffer.BlockCopy(header, 0, packet, 0, header.Length * 4);
            Buffer.BlockCopy(data, 0, packet, header.Length * 4, len);
            return packet;
        }


        private static byte[] MakePingPacket(UInt32 userid, UInt32 deviceid)
        {
            Random rand = new Random();

            string[] dataStr = { "35.1", "28%", "0" };
            UInt32[] id = { AVR_TEMP, HUMIDITY_SENSOR1, ALARMTYPE };

            int dataLen = 0;
            foreach(string str in dataStr)
            {
                int curLen = str.Length + 1;
                //int rc = curLen % 4;
                //if (rc > 0)
                //    curLen += (4 - rc);

                dataLen += curLen + 4;
            }

            UInt32[] header = new UInt32[]
            {
                0x474B4C53,
                userid,
                deviceid,
                CMD_PING,
                (UInt32)dataLen,
                0,
            };
            header[5] = countCRC(header);

            byte[] packet = new byte[header.Length * 4 + dataLen];

            Buffer.BlockCopy(header, 0, packet, 0, header.Length * 4);
            int offset = header.Length * 4;
            for (int i=0; i<id.Length; i++)
            {
                Buffer.BlockCopy(BitConverter.GetBytes(id[i]), 0, packet, offset, 4);
                offset += 4;
                string val = dataStr[i];
                Buffer.BlockCopy(Encoding.ASCII.GetBytes(val), 0, packet, offset, val.Length);
                int len = val.Length + 1;
                offset += len;
                //int rc = len % 4;
                //if (rc > 0)
                //    offset += (4 - rc);

            }
            return packet;
        }
    }
}


class CRC
{
    UInt32 gCrc;
    UInt32[] memoCrc = new UInt32[5];

    public CRC()
    {
        gCrc = 0xFFFFFFFF;
    }
    public void reset()
    {
        gCrc = 0xFFFFFFFF;
    }

    public UInt32 doCRC(UInt32 dt)
    {
        UInt32 Crc;
        ///   ----------------------------
        //crc = gCrc ^ 0xFFFFFFFF;
        //crc = crc32_tab[(crc ^ dt) & 0xFF] ^ (crc >> 8);
        //gCrc = crc ^ ~0U;
        // ---------------------------------

        Crc = gCrc ^ dt; // Apply all 32-bits

        // Process 32-bits, 4 at a time, or 8 rounds

        Crc = (Crc << 4) ^ CrcTable[Crc >> 28]; // Assumes 32-bit reg, masking index to 4-bits
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28]; //  0x04C11DB7 Polynomial used in STM32
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28];
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28];
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28];
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28];
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28];
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28];


        gCrc = Crc;

        // save in fifo for delayed by 4 bytes check
        memoCrc[4] = memoCrc[3];
        memoCrc[3] = memoCrc[2];
        memoCrc[2] = memoCrc[1];
        memoCrc[1] = memoCrc[0];
        memoCrc[0] = gCrc;
        return gCrc;
    }

    public UInt32 val
    {
        get { return gCrc; }
    }

    public UInt32 last(int n)
    {
        return (memoCrc[n]);
    }

    public bool isCrcOK(UInt32 xCrc)
    {
        return (memoCrc[4] == xCrc);
    }
    public UInt32 doCRC(byte dt)
    {
        return doCRC((UInt32)dt);
    }

    UInt32[] crc32_tab =
    {
    0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419, 0x706af48f,
    0xe963a535, 0x9e6495a3, 0x0edb8832, 0x79dcb8a4, 0xe0d5e91e, 0x97d2d988,
    0x09b64c2b, 0x7eb17cbd, 0xe7b82d07, 0x90bf1d91, 0x1db71064, 0x6ab020f2,
    0xf3b97148, 0x84be41de, 0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7,
    0x136c9856, 0x646ba8c0, 0xfd62f97a, 0x8a65c9ec, 0x14015c4f, 0x63066cd9,
    0xfa0f3d63, 0x8d080df5, 0x3b6e20c8, 0x4c69105e, 0xd56041e4, 0xa2677172,
    0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b, 0x35b5a8fa, 0x42b2986c,
    0xdbbbc9d6, 0xacbcf940, 0x32d86ce3, 0x45df5c75, 0xdcd60dcf, 0xabd13d59,
    0x26d930ac, 0x51de003a, 0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423,
    0xcfba9599, 0xb8bda50f, 0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924,
    0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d, 0x76dc4190, 0x01db7106,
    0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f, 0x9fbfe4a5, 0xe8b8d433,
    0x7807c9a2, 0x0f00f934, 0x9609a88e, 0xe10e9818, 0x7f6a0dbb, 0x086d3d2d,
    0x91646c97, 0xe6635c01, 0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e,
    0x6c0695ed, 0x1b01a57b, 0x8208f4c1, 0xf50fc457, 0x65b0d9c6, 0x12b7e950,
    0x8bbeb8ea, 0xfcb9887c, 0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65,
    0x4db26158, 0x3ab551ce, 0xa3bc0074, 0xd4bb30e2, 0x4adfa541, 0x3dd895d7,
    0xa4d1c46d, 0xd3d6f4fb, 0x4369e96a, 0x346ed9fc, 0xad678846, 0xda60b8d0,
    0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9, 0x5005713c, 0x270241aa,
    0xbe0b1010, 0xc90c2086, 0x5768b525, 0x206f85b3, 0xb966d409, 0xce61e49f,
    0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4, 0x59b33d17, 0x2eb40d81,
    0xb7bd5c3b, 0xc0ba6cad, 0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a,
    0xead54739, 0x9dd277af, 0x04db2615, 0x73dc1683, 0xe3630b12, 0x94643b84,
    0x0d6d6a3e, 0x7a6a5aa8, 0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1,
    0xf00f9344, 0x8708a3d2, 0x1e01f268, 0x6906c2fe, 0xf762575d, 0x806567cb,
    0x196c3671, 0x6e6b06e7, 0xfed41b76, 0x89d32be0, 0x10da7a5a, 0x67dd4acc,
    0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5, 0xd6d6a3e8, 0xa1d1937e,
    0x38d8c2c4, 0x4fdff252, 0xd1bb67f1, 0xa6bc5767, 0x3fb506dd, 0x48b2364b,
    0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60, 0xdf60efc3, 0xa867df55,
    0x316e8eef, 0x4669be79, 0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236,
    0xcc0c7795, 0xbb0b4703, 0x220216b9, 0x5505262f, 0xc5ba3bbe, 0xb2bd0b28,
    0x2bb45a92, 0x5cb36a04, 0xc2d7ffa7, 0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d,
    0x9b64c2b0, 0xec63f226, 0x756aa39c, 0x026d930a, 0x9c0906a9, 0xeb0e363f,
    0x72076785, 0x05005713, 0x95bf4a82, 0xe2b87a14, 0x7bb12bae, 0x0cb61b38,
    0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21, 0x86d3d2d4, 0xf1d4e242,
    0x68ddb3f8, 0x1fda836e, 0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777,
    0x88085ae6, 0xff0f6a70, 0x66063bca, 0x11010b5c, 0x8f659eff, 0xf862ae69,
    0x616bffd3, 0x166ccf45, 0xa00ae278, 0xd70dd2ee, 0x4e048354, 0x3903b3c2,
    0xa7672661, 0xd06016f7, 0x4969474d, 0x3e6e77db, 0xaed16a4a, 0xd9d65adc,
    0x40df0b66, 0x37d83bf0, 0xa9bcae53, 0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9,
    0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6, 0xbad03605, 0xcdd70693,
    0x54de5729, 0x23d967bf, 0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94,
    0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d
    };


    UInt32[] CrcTable = { // Nibble lookup table for 0x04C11DB7 polynomial
    0x00000000,0x04C11DB7,0x09823B6E,0x0D4326D9,0x130476DC,0x17C56B6B,0x1A864DB2,0x1E475005,
    0x2608EDB8,0x22C9F00F,0x2F8AD6D6,0x2B4BCB61,0x350C9B64,0x31CD86D3,0x3C8EA00A,0x384FBDBD };

    public UInt32 CrcSTM32(byte Data)
    {

        UInt32 Crc = gCrc;
        Crc = Crc ^ Data; // Apply all 32-bits

        // Process 32-bits, 4 at a time, or 8 rounds

        Crc = (Crc << 4) ^ CrcTable[Crc >> 28]; // Assumes 32-bit reg, masking index to 4-bits
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28]; //  0x04C11DB7 Polynomial used in STM32
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28];
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28];
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28];
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28];
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28];
        Crc = (Crc << 4) ^ CrcTable[Crc >> 28];
        gCrc = Crc;
        return (Crc);
    }
}

class DevCommand
{
    public UInt32 userid;
    public UInt32 device;
    public UInt32 cmd;
    public UInt32 dataLen;
    public byte[] data;

    public bool Read(NetworkStream str)
    {
        byte[] buffer = new byte[6 * 4];
        int rc = str.Read(buffer, 0, buffer.Length);
        if (rc <= 0)
            return false;

        if (rc >= 6 * 4)
        {
            int index = 0;
            UInt32 tag = BitConverter.ToUInt32(buffer, 4 * index++);
            userid = BitConverter.ToUInt32(buffer, 4 * index++);
            device = BitConverter.ToUInt32(buffer, 4 * index++);
            cmd = BitConverter.ToUInt32(buffer, 4 * index++);
            dataLen = BitConverter.ToUInt32(buffer, 4 * index++);
            UInt32 crc = BitConverter.ToUInt32(buffer, 4 * index++);
            if (dataLen > 0)
            {
                data = new byte[dataLen];
                str.Read(data, 0, (int)dataLen);
            }
            else
            {
                data = null;
            }
        }

        return true;
    }

}