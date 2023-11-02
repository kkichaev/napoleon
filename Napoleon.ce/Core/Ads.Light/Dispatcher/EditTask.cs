using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Xml.Serialization;
using System.IO;
using GRSoft.Network;

namespace GRSoft.Ads.Dispatcher
{
   public partial class EditTask : Form
   {
      DataSet<string, Question> dsQuestion;

      public EditTask()
      {
         InitializeComponent();
         dsQuestion = (DataSet<string, Question>)DataModule.Get(Question.OBJECT_NAME);
      }

      public delegate bool CheckSaveTask();

      public Agent AgentTask {set { tbName.Text = value.Name; } }
      public DateTime Start { get { return dtpStart.Value; } set { dtpStart.Value = value; } }
      public DateTime Finish { get { return dtpFinish.Value; } set { dtpFinish.Value = value; } }
      public string Task { get { return tbTask.Text.Trim(); } set { tbTask.Text = value; } }
      public CheckSaveTask SaveTaskChecker;
      public string ClientName { get { return tbClientName.Text.Trim(); } set { tbClientName.Text = value; } }
      public string ClientPhone { get { return tbPhone.Text.Trim(); } set { tbPhone.Text = value; } }
      public string City { get { return cbCity.Text.Trim(); } set { cbCity.Text = value; } }
      public string Street { get { return cbStreet.Text.Trim(); } set { cbStreet.Text = value; } }
      public string House { get { return tbHouse.Text.Trim(); } set { tbHouse.Text = value; } }
      public List<TaskQuest> Questions { get { return GetQuestioons(); } set { SetQuestions(value); } }
      private List<string> taskQuestions = new List<string>();

      private List<TaskQuest> GetQuestioons()
      {
         List<TaskQuest> result = new List<TaskQuest>();

         foreach(object sel in cbQuestion.CheckedItems)
         {
            Question q = (Question)sel;
            TaskQuest tq = new TaskQuest();

            tq.id = q.idquest;
            result.Add(tq);
         }

         return result;
      }

      private void SetQuestions(List<TaskQuest> value)
      {
         taskQuestions.Clear();
         foreach (TaskQuest tq in value)
            taskQuestions.Add(tq.id);
      }

      private EditTaskData editTaskData = new EditTaskData();

      [Serializable]
      public class EditTaskData
      {
         public static readonly string OBJECT_NAME = "etd.dat";
         public List<string> cities = new List<string>();
         public List<string> streets = new List<string>();
      }

      private void EditTask_Load(object sender, EventArgs e)
      {
         XmlSerializer ser = new XmlSerializer(typeof(EditTaskData)); 
         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
         string fileName = v + Config.FOLDER + EditTaskData.OBJECT_NAME;

         if (File.Exists(fileName))
            try
            {
               using (FileStream fs = new FileStream(fileName, FileMode.Open, FileAccess.Read))
               {
                  try
                  {
                     editTaskData = (EditTaskData)ser.Deserialize(fs);

                     foreach (string c in editTaskData.cities)
                        cbCity.Items.Add(c);

                     foreach (string s in editTaskData.streets)
                        cbStreet.Items.Add(s);

                     cbCity.Sorted = true;
                     cbStreet.Sorted = true;
                     fs.Close();
                  }
                  catch{}
               }
            }
            catch {}

         foreach (Question q in dsQuestion.Data)
            cbQuestion.Items.Add(q);

         for (int i = 0; i < cbQuestion.Items.Count; i++)
         {
            Question q = (Question)cbQuestion.Items[i];
            if (taskQuestions.Contains(q.idquest))
               cbQuestion.SetItemChecked(i, true);
         }
      }

      private void EditTask_FormClosed(object sender, FormClosedEventArgs e)
      {
         XmlSerializer ser = new XmlSerializer(typeof(EditTaskData));
         string v = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Config.FOLDER;
         
         if (!Directory.Exists(v))
            Directory.CreateDirectory(v);

         string fileName = v + EditTaskData.OBJECT_NAME;
         try
         {
            using (FileStream fs = new FileStream(fileName, FileMode.OpenOrCreate, FileAccess.Write))
            {
               try
               {
                  if (!editTaskData.cities.Contains(cbCity.Text.Trim()))
                     editTaskData.cities.Add(cbCity.Text.Trim());

                  if (!editTaskData.streets.Contains(cbStreet.Text.Trim()))
                     editTaskData.streets.Add(cbStreet.Text.Trim());

                  ser.Serialize(fs, editTaskData);
                  fs.Close();
               }
               catch
               {
               }
            }
         }catch{}
      }

      private void EditTask_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK && SaveTaskChecker != null)
         {
            e.Cancel = !SaveTaskChecker();
         }
      }
   }
}
