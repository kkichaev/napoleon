using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Threading;

namespace PrjConv
{
   public delegate void InvokeDelegate();

   public partial class fmMain : Form
   {
      private FileList fileList;

      public fmMain()
      {
         InitializeComponent();
         fileList = new FileList(lbFileList);
         cbTarget.SelectedIndex = 0;
      }

      private void btnBrowse_Click(object sender, EventArgs e)
      {
         fileList.Clear();
         
         FolderBrowserDialog folderBrowseDialog = new FolderBrowserDialog();
         if (folderBrowseDialog.ShowDialog() == DialogResult.OK)
         {
            MakeConversionList(folderBrowseDialog.SelectedPath);
         }
      }

      private void MakeConversionList(string path)
      {
         const string PRJ_MASK = "*.csproj";
         const string SLN_MASK = "*.sln";

         tbPath.Text = path;

         string[] solutionFiles = Directory.GetFiles(path, SLN_MASK, SearchOption.AllDirectories);
         string[] projectFiles = Directory.GetFiles(path, PRJ_MASK, SearchOption.AllDirectories);

         fileList.AddFiles(solutionFiles, projectFiles);
      }

      private void DoneConversionForSolutions(ConversionMap cm)
      {
         foreach (string s in fileList.Solutions)
         {
            Converter.makeSolConv(s, cm.solConv);
            BeginInvoke(new InvokeDelegate(delegate { StepProcess(s); }));
         }
      }

      private void DoneConversionForProjects(ConversionMap cm)
      {
         foreach (string s in fileList.Projects)
         {
            Converter.makePrjConv(s, cm.prjConv, cm.prjConvTextVal);
            BeginInvoke(new InvokeDelegate(delegate { StepProcess(s); }));
         }
      }

      private void DoneConversion(string target)
      {
         ConversionMap cm = ConversionMapFactory.GetMap(target);
         DoneConversionForSolutions(cm);
         DoneConversionForProjects(cm);
         BeginInvoke(new InvokeDelegate(delegate { EndProcess(); }));
      }

      private void StepProcess(string s)
      {
         lbLog.Items.Add(s + " DONE!");
         progressBar.Value += 1;
      }

      private void BeginProcess()
      {
         lbLog.Items.Clear();
         progressBar.Maximum = fileList.Count;
         progressBar.Value = 0;
         SetControlEnabled(false);
      }

      private void SetControlEnabled(bool enable)
      {
         tbPath.Enabled = enable;
         cbTarget.Enabled = enable;
         btnBrowse.Enabled = enable;
         btnDoneConversion.Enabled = enable;
      }

      private void EndProcess()
      {
         lbLog.Items.Add("conversion SUCCESS");
         SetControlEnabled(true);

         if (cbClose.Checked)
         {
            Application.Exit();
         }
      }

      private void btnDoneConversion_Click(object sender, EventArgs e)
      {
         BeginProcess();
         string target = cbTarget.Items[cbTarget.SelectedIndex].ToString(); 
         new Thread(new ThreadStart(delegate { DoneConversion(target); })).Start();
      }

      private void fmMain_Load(object sender, EventArgs e)
      {
         const string NAPOLEON_SYSTEM = "NAPOLEON_SYSTEM";
         string system_path = Environment.GetEnvironmentVariable(NAPOLEON_SYSTEM);

         if (system_path != null && system_path.Trim() != string.Empty)
         {
            MakeConversionList(system_path);
         }
      }

      private class FileList
      { 
         private List<string> solList = new List<string>();
         private List<string> prjList = new List<string>();
         private ListBox fileListBox;

         public FileList(ListBox listBox)
         {
            fileListBox = listBox;
         }

         public void AddRangeSol(IEnumerable<string> range)
         {
            solList.AddRange(range);
         }

         public void AddRangePrj(IEnumerable<string> range)
         {
            prjList.AddRange(range);
         }

         public void Clear()
         {
            solList.Clear();
            prjList.Clear();
            fileListBox.Items.Clear();
         }

         public void AddFiles(IEnumerable<string> sol, IEnumerable<string> prj)
         {
            fileListBox.Items.Clear();
            AddRangeSol(sol);
            AddRangePrj(prj);
            fileListBox.Items.AddRange(Files.ToArray());
         }

         private List<string> Files
         {
            get
            {
               List<string> result = new List<string>(solList);
               result.AddRange(prjList);
               return result;
            }
         }

         public List<string> Solutions { get { return solList; } }
         public List<string> Projects { get{ return prjList; } }
         public int Count { get { return Files.Count; } }
      }
   }
}