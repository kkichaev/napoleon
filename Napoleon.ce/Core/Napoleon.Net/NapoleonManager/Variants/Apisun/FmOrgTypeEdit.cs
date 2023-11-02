using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgTypeEdit : Form
   {
      public FmOrgTypeEdit()
      {
         InitializeComponent();
      }

      public static OrgType Edit(OrgType orgType)
      {
         OrgType result = null;
         FmOrgTypeEdit form = new FmOrgTypeEdit();

         if (orgType != null)
         {
            form.tbId.Text = orgType.Id;
            form.tbName.Text = orgType.Name;
            form.tbId.Enabled = false;
         }
         else
            form.tbId.Text = GenNewId();

         if (form.ShowDialog() == DialogResult.OK)
         {
            result = orgType ?? new OrgType();

            if (orgType == null)
               result.id = form.tbId.Text.Trim();

            result.name = form.tbName.Text.Trim();
         }

         return result;
      }

      private static string GenNewId()
      {
         DataSet<string, OrgType> dsOrgType = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME);
         string result = "1";

         if (dsOrgType != null)
         {
            int maxNumber = 0;

            foreach(OrgType ot in dsOrgType.Data)
            {
               int val = 0;
               if (Int32.TryParse(ot.id, out val))
                  if (val > maxNumber)
                     maxNumber = val;
            }

            result = (++maxNumber).ToString();
         }

         return result;
      }
   }
}
