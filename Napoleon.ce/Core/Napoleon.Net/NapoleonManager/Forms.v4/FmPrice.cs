using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.UILib;
using GRSoft.NapoleonManager.Utils;
using System.IO;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.Threading;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmPrice : Form
   {
      public FmPrice()
      {
         InitializeComponent();
         __Initing();
      }
   }
}
