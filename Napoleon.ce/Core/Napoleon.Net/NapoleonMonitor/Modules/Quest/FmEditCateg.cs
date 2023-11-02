using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmEditCateg : Form
   {
      public FmEditCateg()
      {
         InitializeComponent();
      }

      public static Category Open(Category category)
      {
         Category result = null;
         FmEditCateg instance = new FmEditCateg();

         if (category != null)
            instance.tbName.Text = category.name;

         if (instance.ShowDialog() == DialogResult.OK)
         {
            if (category == null)
            {
               result = new Category();
               result.id = System.Guid.NewGuid().ToString().Replace("-","");
            }
            else
               result = category;

            result.name = instance.tbName.Text;
         }

         return result;
      }
   }
}
