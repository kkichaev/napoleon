using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Threading;
using System.Data;
using GRSoft.Network;
using System.ComponentModel;
using System.Collections;

namespace GRSoft.NapoleonAdmin
{
    public delegate void DataRetrieveComplete();

    public class UserLogDataRep
    {
        private DateTime date;
        private string userName;
        private string objectType;

        public DateTime Date { get { return date; } set { date = value; } }
        public string UserName { get { return userName; } set { userName = value; } }
        public string ObjectType { get { return objectType; } set { objectType = value; } }

        public UserLogDataRep(DateTime date, string userName, string objectType)
        {
            Date = date;
            UserName = userName;
            ObjectType = objectType;
        }
    }

    //public class TblUserLog : TableAdapter<List<UserLogDataRep>>, ITables
    public class TblUserLog : List<UserLogDataRep>
    {
        private DBConnection server;
        private string userName;
        private string password;
        private DateTime logDate;

        private const string TABLE_NAME = "UserLog";

        public TblUserLog(DBConnection server)
            :base(new List<UserLogDataRep>())
        {
            this.server = server;
        }

        public void add(System.Object record)
        {
           //data.Add(record as UserLogDataRep);
           (this as List<UserLogDataRep>).Add(record as UserLogDataRep);
        }

        public DateTime LogDate
        {
           get { return logDate; }
           set { logDate = value; }
        }

        public void refresh(string userName, string password, bool wait)
        {
            this.userName = userName;
            this.password = password;

            //ObjectList objectList = new GetCommand(userName, password,
            //    new string[] { TABLE_NAME });

            string filter = String.Format("date >= ToDate('{0}') and date < ToDate('{1}')",
               logDate.ToString("dd/MM/yyyy"),
               logDate.AddDays(1).ToString("dd/MM/yyyy"));
            SelectCommand objectList = new SelectCommand(userName, password, TABLE_NAME, filter);
            //objectList.Add(objectList, filter);

            //server.SendCommand(new SendParam(objectList, fetchData), wait);
        }

        public void fetchData(PacketObject source)
        {
            //Clear();
            ////data.Clear();
            //ObjectList list = source[TABLE_NAME];
            //TblAgents tblAgents = new TblAgents(server);
            //tblAgents.refresh(userName, password, true);
            

            //foreach (GRSoft.Network.Object record in list)
            //{
            //    try
            //    {
            //        add(new UserLogDataRep(Convert.ToDateTime(record["date"].Value),
            //             tblAgents[Convert.ToInt32(record["id"].Value)].Name,
            //            //record["id"].Value.ToString(),
            //            record["objType"].Value.ToString()));
            //    }
            //    catch(Exception except)
            //    {
            //       Console.WriteLine(except.ToString());
            //    }
            //}

            //if (OnDataRetrieveComplete != null)
            //{
            //    OnDataRetrieveComplete();
            //}
        }

        //public List<UserLogDataRep> Data { get { return this; } }
        //public List<UserLogDataRep> Data { get { return data; } }
        //public event DataRetrieveComplete OnDataRetrieveComplete;
    }
}
