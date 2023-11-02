using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FmMatrixDesigner : Form
   {
      public FmMatrixDesigner()
      {
         InitializeComponent();
         __Initing();
      }
   }
}