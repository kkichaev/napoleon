using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class ScriptRes : UserControl
   {
      public ScriptRes()
      {
         InitializeComponent();
      }

      internal void setData(List<global::GRSoft.NapoleonManager.FmDetailEx.Data> list)
      {
         grid.DataSource = list;
      }
   }
}
