using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmQuestEdit : Form
   {
      public FmQuestEdit(Question question)
      {
         InitializeComponent();
         __Initing(question);
      }
   }
}
