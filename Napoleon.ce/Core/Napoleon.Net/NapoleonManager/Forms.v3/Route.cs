using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Globalization;
using System.Net;
using System.IO;
using System.Xml;
using GRSoft.Network;
using GRSoft.NapoleonManager.Maps;
using GRSoft.NapoleonManager.Utils;
using System.Reflection;
using System.Security.Cryptography.X509Certificates;
using System.Net.Security;
using System.Collections.ObjectModel;

namespace GRSoft.NapoleonManager
{

   public partial class Route : Form, IRoute, FmSelectContrAgent.Selected
   {
      public Route()
      {
         InitializeComponent();
         __Initing();
      }
   }
}
