using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmQuestAttach : Form
   {
      public List<QuestionAttach> Attach { get; set; }
      private DataSet<string, Attachment> dsAttach = new DataSet<string, Attachment>(Attachment.OBJECT_NAME,false);

      public FmQuestAttach()
      {
         InitializeComponent();
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         OpenFileDialog dlg = new OpenFileDialog();

         if (dlg.ShowDialog() == System.Windows.Forms.DialogResult.OK)
         {
            Attachment at = new Attachment();
            at.id = Attachment.GenId();
            at.data = File.ReadAllBytes(dlg.FileName);
            at.name = dlg.SafeFileName;
            DataSet<string, Attachment> ds = new DataSet<string,Attachment>(Attachment.OBJECT_NAME);
            ds.Add(at.id, at);

            List<IDataSet> write = new List<IDataSet>();
            write.Add(ds);

            if (DataModule.UpdateDataSet(write, null, null, Config.GetConfig().GetConnection()))
            {
               QuestionAttach qa = new QuestionAttach();
               qa.id = at.id;
               qa.name = at.name;
               Attach.Add(qa);

               listBox.Items.Add(qa);
            }else
               MessageBox.Show("Ошибка записи в БД");

         }
      }

      private void FmQuestAttach_Load(object sender, EventArgs e)
      {
         listBox.Items.Clear();

         foreach (QuestionAttach qa in Attach)
            listBox.Items.Add(qa);
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         QuestionAttach qa = listBox.SelectedItem as QuestionAttach;

         if (qa != null)
         {
            Attach.Remove(qa);
            listBox.Items.Remove(qa);
         }
      }

      private void listBox_MouseDoubleClick(object sender, MouseEventArgs e)
      {
         QuestionAttach qa = listBox.SelectedItem as QuestionAttach;

         if (qa != null)
         {
            dsAttach.Clear();
            dsAttach.Filter = String.Format("\"id\"='{0}'", qa.id);
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsAttach);
            FmWait.StdDataRefresh(this, upd, OpenAttachment);
         }
      }

      private void OpenAttachment()
      {
         if (dsAttach.Values.Count > 0)
         {
            foreach (string id in dsAttach.Keys) 
            {
               Attachment a = dsAttach[id];

               if (a != null)
               {
                  string name = Path.GetTempPath() + a.name;
                  File.WriteAllBytes(name, a.data);
                  System.Diagnostics.Process.Start(name);

                  break;
               }
            }
         }

      }
   }
}
