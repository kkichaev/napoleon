using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;
using System.Xml;

namespace SymbolsExtractor
{
   public partial class FmIntFontSelect : Form
   {
      Form1 owner;
      string lang = "";
      string langFileName = "";
      string helpFolder = "";
      string fontFolder = "";

      public FmIntFontSelect()
      {
         InitializeComponent();
      }

      public void setData(string fileName, string outFolder, string fontFolder, Form1 owner)
      {
         this.owner = owner;
         this.fontFolder = fontFolder;


         XmlDocument doc = new XmlDocument();
         doc.Load(fileName);
         foreach (XmlNode n in doc.GetElementsByTagName("ConfigOptions"))
         {
            lang = n["languege"].InnerText;
            break;
         }

         label1.Text = lang;

         DirectoryInfo di = new DirectoryInfo(fontFolder);
         FileInfo[] files = di.GetFiles("*.ttf");

         foreach (FileInfo fi in files)
         {
            cbFonts.Items.Add(fi.Name);
         }

      }

      private void button1_Click(object sender, EventArgs e)
      {
         OpenFileDialog ofd = new OpenFileDialog();
         ofd.Filter = "Languages files (*.txt)|*.txt";
         if (ofd.ShowDialog() == System.Windows.Forms.DialogResult.OK)
         {
            langFileName = ofd.FileName;
            label5.Text = langFileName;
         }
      }

      private void button2_Click(object sender, EventArgs e)
      {
         FolderBrowserDialog fbd = new FolderBrowserDialog();
         if (fbd.ShowDialog() == DialogResult.OK)
         {
            helpFolder = fbd.SelectedPath;
            label4.Text = helpFolder;
         }
      }

      int FindLangIndex(string line)
      {
         int res = 0;
         foreach(string p in line.Split(new char[] {'\t'}))
         {
            if (p == lang) return res;
            res++;
         }
         return -1;
      }

      private void button3_Click(object sender, EventArgs e)
      {
         if(cbFonts.SelectedItem == null)
         {
            MessageBox.Show("Select font before, please");
            return;
         }

         string[] lines = File.ReadAllLines(langFileName);
         if (lines.Length < 2)
         {
            MessageBox.Show("Wrong language file");
            return;
         }

         int li = FindLangIndex(lines[1]);
         if(li < 0)
         {
            MessageBox.Show("Can't find language in file");
            return;
         }

         List<int> syms = new List<int>();

         for(int i=2; i<lines.Length; i++)
         {
            string[] parts = lines[i].Split(new char[] { '\t' });
            if(li < parts.Length)
            {
               Form1.AddSymbols(syms, parts[li]);
            }
         }
         Form1.langSymbols.Clear();
         Form1.langSymbols.AddRange(syms);

         if(helpFolder.Length  > 0)
         {
            DirectoryInfo di = new DirectoryInfo(helpFolder);
            FileInfo[] files = di.GetFiles("*.*");
            foreach(FileInfo f in files)
            {
               foreach(string s in  File.ReadAllLines(f.FullName))
               {
                  Form1.AddSymbols(syms, s);
               }
            }
         }

         FontData fd = new FontData();
         fd.Size = (int)numericUpDown1.Value;

         string font = (string)cbFonts.SelectedItem;
         fd.FontFile = fontFolder + "\\" + font;
         fd.FontName = font.Split(new char[] { '.' })[0];
         fd.AddFileBase = "_internal";

         Dictionary<FontData, List<int>> d = new Dictionary<FontData, List<int>>();
         d[fd] = syms;
         owner.WriteFiles(d, false);
      }
   }
}
