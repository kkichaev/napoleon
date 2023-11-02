using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using System.Globalization;
using System.Collections;
using System.IO;
using System.Reflection;
using GRSoft.NapoleonManager.Utils;
using System.Net;

namespace GRSoft.NapoleonManager
{

   public partial class FmDetailBase : Form
   {
      public FmDetailBase(FmDetailData detailData)
      {
         InitializeComponent();
         __Initing(detailData);
      }
   }
}