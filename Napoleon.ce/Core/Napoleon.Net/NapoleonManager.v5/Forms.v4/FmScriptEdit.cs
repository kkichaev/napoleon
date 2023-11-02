using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using GRSoft.NapoleonManager.Properties;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmScriptEdit : Form
   {
      protected FmScriptEdit(PostProcess postProcess)
      {
         InitializeComponent();
         __Initing(postProcess);
      }
   }
}
