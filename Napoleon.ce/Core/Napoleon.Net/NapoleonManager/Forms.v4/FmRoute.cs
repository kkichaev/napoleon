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
using System.Windows.Forms.VisualStyles;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public partial class FmRoute : Form
   {
      public FmRoute(string idAgent, DateTime date)
      {
         InitializeComponent();
         __Initing(idAgent, date);
      }
   }
}