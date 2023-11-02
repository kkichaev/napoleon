using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace SymbolsExtractor
{
   public partial class SelectFont : Form
   {
      string selectedFile;
      string folder;
      public SelectFont()
      {
         InitializeComponent();
      }

      public void SetFolder(string folder)
      {
         this.folder = folder;
         DirectoryInfo di = new DirectoryInfo(folder);
         FileInfo[] files = di.GetFiles("*.ttf");

         foreach(FileInfo fi in files)
         {
            lvFiles.Items.Add(fi.Name);
         }
      }

      public string SelectedFile { get { return selectedFile; } }

      private void lvFiles_DoubleClick(object sender, EventArgs e)
      {
         if(lvFiles.SelectedItems.Count == 1)
         {
            selectedFile = folder + "\\" + lvFiles.SelectedItems[0].Text;
            DialogResult = System.Windows.Forms.DialogResult.OK;
         }
      }

      private void button2_Click(object sender, EventArgs e)
      {
         if (lvFiles.SelectedItems.Count == 1)
         {
            selectedFile = folder + "\\" + lvFiles.SelectedItems[0].Text;
            DialogResult = System.Windows.Forms.DialogResult.OK;
         }
      }
   }
}
