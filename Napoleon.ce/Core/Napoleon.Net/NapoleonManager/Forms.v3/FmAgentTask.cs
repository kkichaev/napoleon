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
using System.IO;
using System.Collections;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentTask : Form
   {
      public FmAgentTask()
      {
         InitializeComponent();
         __Initing();
      }
   }
}
