using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class ViewException : Form
   {
      public ViewException()
      {
         InitializeComponent();
      }

      public Exception Exception
      {
         set
         {
            if (value.Message != null)
               tbMessage.Text = value.Message;

            StringBuilder sb = new StringBuilder();
            var trace = new System.Diagnostics.StackTrace(value);
            foreach (var frame in trace.GetFrames())
            {
               var method = frame.GetMethod();
               if (method.Name.Equals("LogStack")) continue;
               
               sb.AppendFormat("{0}::{1} iloffset {2}\n",
                   method.ReflectedType != null ? method.ReflectedType.Name : string.Empty,
                   method.Name,
                   frame.GetILOffset()
                   );
            }
            tbStack.Text = sb.ToString();
         }
      }
   }
}
