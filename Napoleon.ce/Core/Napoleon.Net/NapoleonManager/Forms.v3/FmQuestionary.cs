using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.IO;

namespace GRSoft.NapoleonManager
{
   public partial class FmQuestionary : Form, IQuestFactory
   {
      public FmQuestionary()
      {
         InitializeComponent();
         __Initing();
      }
   }
}
