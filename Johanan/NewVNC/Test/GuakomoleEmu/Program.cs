using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace GuakomoleEmu
{
    class Program
    {
        static string SERVER = "127.0.0.1";

        static uint USERID = 48731;
        static uint DEVICE = 111;

        static readonly UInt32 CMD_PHP_VNC_REQ = 100;
        static readonly UInt32 CMD_PHP_VNC_ACCEPT = 102;

        static void Main(string[] args)
        {
            TcpClient cli = new TcpClient();
            try
            {

                cli.Connect(SERVER, 7654);
                NetworkStream str = cli.GetStream();
                str.ReadTimeout = 10 * 1000;

                byte[] pkt = MakeReqVNCPacket(USERID, DEVICE);
                str.Write(pkt, 0, pkt.Length);

                while (cli.Available == 0)
                {
                    Thread.Sleep(5);
                }
                DevCommand dc = new DevCommand();
                if (dc.Read(str))
                {
                    if(dc.cmd == CMD_PHP_VNC_ACCEPT)
                    {
                        int port = BitConverter.ToInt32(dc.data, 0);

                        cli.Close();

                        Console.WriteLine("Start VNC port " + port.ToString());

                        TcpListener server = new TcpListener(IPAddress.Parse("0.0.0.0"), 7070);
                        server.Start();
                        cli = server.AcceptTcpClient();
                        str = cli.GetStream();

                        TcpClient vncCli = new TcpClient(SERVER, port);
                        NetworkStream vncStr = vncCli.GetStream();
                        while (true)
                        {
                            while (cli.Available == 0 && vncCli.Available == 0)
                            {
                                if(!cli.Connected)
                                {
                                    vncCli.Close();
                                    break;
                                }
                                Thread.Sleep(5);
                            }

                            while (vncCli.Available > 0)
                            {
                                byte[] buffer = new byte[vncCli.Available];
                                int rc = vncStr.Read(buffer, 0, buffer.Length);
                                if (rc > 0)
                                {
                                    //Console.WriteLine("Got from VNC  " + rc.ToString());
                                    str.Write(buffer, 0, rc);
                                }

                            }
                            while (cli.Available > 0)
                            {
                                byte[] buffer = new byte[cli.Available];
                                int rc = str.Read(buffer, 0, buffer.Length);
                                if (rc > 0)
                                {
                                    //Console.WriteLine("Send to VNC  " + rc.ToString());
                                    vncStr.Write(buffer, 0, rc);
                                }
                            }
                        }
                    }
                }

            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
            

        }

        private static byte[] MakeReqVNCPacket(UInt32 userid, UInt32 deviceid)
        {
            Random rand = new Random();

            int len = 0;

            UInt32[] header = new UInt32[]
            {
                0x474B4C53,
                userid,
                deviceid,
                CMD_PHP_VNC_REQ,
                (UInt32)len,
            };

            byte[] packet = new byte[header.Length * 4 + len];
            Buffer.BlockCopy(header, 0, packet, 0, header.Length * 4);
            return packet;
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
            byte[] buffer = new byte[5 * 4];
            int rc = str.Read(buffer, 0, buffer.Length);
            if (rc <= 0)
                return false;

            if (rc >= 5 * 4)
            {
                int index = 0;
                UInt32 tag = BitConverter.ToUInt32(buffer, 4 * index++);
                userid = BitConverter.ToUInt32(buffer, 4 * index++);
                device = BitConverter.ToUInt32(buffer, 4 * index++);
                cmd = BitConverter.ToUInt32(buffer, 4 * index++);
                dataLen = BitConverter.ToUInt32(buffer, 4 * index++);
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

}

