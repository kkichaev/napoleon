using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Globalization;
using GRSoft.NapoleonManager.Maps;
using System.IO;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Reports.Excel;
using System.Threading;

namespace GRSoft.NapoleonManager
{
   public partial class FmCoverArea : Form
   {
      
      public FmCoverArea(string idAgent, DateTime date)
      {
         InitializeComponent();
         __Initing(idAgent, date);
      }
   }
}