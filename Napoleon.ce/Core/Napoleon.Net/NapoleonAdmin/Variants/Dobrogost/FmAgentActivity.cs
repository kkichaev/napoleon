using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonAdmin
{
   public partial class FmAgentActivity : UserControl
   {
      public FmAgentActivity()
      {
         InitializeComponent();
         dgvItems.AutoGenerateColumns = false;
         dgvItems.ClipboardCopyMode = DataGridViewClipboardCopyMode.EnableAlwaysIncludeHeaderText;
      }

      public void RefreshData(SimpleDataSet<AgentActivity> agents)
      {
         List<Data> src = new List<Data>();

         foreach(AgentActivity a in agents.Data)
            src.Add(new Data(a));

         dgvItems.DataSource = new BindingList<Data>(src);
      }

      class Data
      {
         AgentActivity agent;

         public Data(AgentActivity agent)
         {
            this.agent = agent;
         }

         public string UserID { get { return agent.userid; } }
         public string Login { get { return agent.login; } }
         public string Phone { get { return agent.phone; } }
         public string IMEI { get { return agent.imei; } }
         public string Date { get { return agent.sended.ToShortDateString(); } }
         public string Time { get { return agent.sended.ToString("HH:MM"); } }

      }

      private void tsbCopy_Click(object sender, EventArgs e)
      {
         dgvItems.SelectAll();
         System.Windows.Forms.DataObject dobj = dgvItems.GetClipboardContent();
         System.Windows.Forms.DataObject dest = new System.Windows.Forms.DataObject("UnicodeText", dobj.GetData("UnicodeText"));

         Clipboard.SetDataObject(dest);

         //BindingList<Data> list = (BindingList<Data>)dgvItems.DataSource;
         //if( list != null )
         //{
         //   foreach(Data data in list)
         //   {
         //   }
         //}
      }
   }
}
