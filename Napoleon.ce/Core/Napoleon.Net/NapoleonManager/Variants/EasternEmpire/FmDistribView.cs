using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmDistribView : UserControl, DataObjectViewer
   {
      public FmDistribView()
      {
         InitializeComponent();
      }

      public void SetData(Network.DataObject dataObject)
      {
         Distrib dd = dataObject as Distrib;
         if (dd != null)
         {
            String text = String.Format("Наши фейсы: {0}\nЧужие фейсы: {1}", dd.outFaces, dd.theirFaces);
            label1.Text = text;
         }
      }
   }
}
