using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmRegionEdit : Form
   {
      public FmRegionEdit()
      {
         InitializeComponent();
      }

      public static LiveArea Open(LiveArea area)
      {
         FmRegionEdit instance = new FmRegionEdit();
         LiveArea result = null;

         if (area != null)
         {
            instance.tbCode.Text = area.code;
            instance.tbName.Text = area.name;
         }

         if (instance.ShowDialog() == DialogResult.OK)
         {
            if (area == null)
            {
               result = new Region();
               result.id = System.Guid.NewGuid().ToString().Replace("-","");
            }
            else
               result = area;

            result.code = instance.tbCode.Text;
            result.name = instance.tbName.Text;
         }

         return result;
      }
   }
}
