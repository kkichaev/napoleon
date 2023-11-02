using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Napoleon
{
   /// <summary>
   /// Interaction logic for WaitWindow.xaml
   /// </summary>
   public partial class WaitWindow : Window, IProgress
   {
      private Thread process;

      public WaitWindow()
      {
         InitializeComponent();
      }

      public Thread Process
      {
         get 
         {
            return process;
         }

         set
         {
            process = value;
         }
      }

      public IProgress ProgressIndicator
      {
         get { return this; }
      }

      public void SetText(string text)
      {
         //throw new NotImplementedException();
      }

      public void SetMax(int max)
      {
         //throw new NotImplementedException();
      }

      public void AdvancePos(int pos)
      {
         //throw new NotImplementedException();
      }
   }
}
