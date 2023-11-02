using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FmClearBase : Form
   {
      public FmClearBase()
      {
         InitializeComponent();
      }

      private void btnStart_Click(object sender, EventArgs e)
      {
         string filter = String.Format(":created >= ToDate('{0}') and created < ToDate('{1}')",
            dtpFrom.Value.Date.Date, dtpTill.Value.Date.Date.AddDays(1));

         List<string> writed = new List<string>();
         PacketObject po = new PacketObject();

         DBConnection conn = Config.GetConfig().GetConnection();
         ServerCommand cmdDelAnswer = new ServerCommand(Commands.REMOVE, Answer.OBJECT_NAME + filter);
         cmdDelAnswer.Password = conn.password;
         cmdDelAnswer.UserID = conn.login;
         po.Add(cmdDelAnswer);

         ServerCommand cmdDelVisit = new ServerCommand(Commands.REMOVE, Visit.OBJECT_NAME + filter);
         cmdDelVisit.Password = conn.password;
         cmdDelVisit.UserID = conn.login;
         po.Add(cmdDelVisit);

         writed.Add(Answer.OBJECT_NAME);
         writed.Add(Visit.OBJECT_NAME);
         Thread t = conn.SendCommand(new SendParamStr(po, DataModule.CheckWrited, writed));
         t.Join();

         if (writed.Count == 0)
            MessageBox.Show("Удаление завершено успешно", "Сообщение", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
         else
            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
      }
   }
}
