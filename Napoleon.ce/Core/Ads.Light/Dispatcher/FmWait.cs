using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Threading;
using GRSoft.Network;

namespace GRSoft.Ads.Dispatcher
{
   public partial class FmWait : Form
   {
      /// <summary>
      /// Екземпляр формы
      /// </summary>
      internal static FmWait instance;

      /// <summary>
      /// Контролируемый тред
      /// </summary>
      private Thread thread;

      private static ReaderWriterLock rwl = new ReaderWriterLock();
      private const int MAX_PROGRESS_VAL = 100;
      private const int TIMER_INTERVAL = 50;
      private const int START_PROGRESS_VAL = 0;
      private int progressPos = 0;
      private System.Windows.Forms.Timer progressTimer;

      private FmWait(Form parent)
      {
         InitializeComponent();
         TopLevel = false;

         Point newOrg = new Point();
         newOrg.X = (parent.Width - Width) / 2;
         newOrg.Y = (parent.Height - Height) / 2;

         parent.Controls.Add(this);
         parent.FormClosing += new FormClosingEventHandler(parent_FormClosing);

         Location = newOrg;
         BringToFront();

         //TopMost = true;
      }

      void parent_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (thread != null)
            StopThread();
      }

      /// <summary>
      /// Отменить выборку данных и закрыть окно ожидания
      /// </summary>
      /// <param name="sender"></param>
      /// <param name="e"></param>
      private void btnCancel_Click(object sender, EventArgs e)
      {
         if (thread != null)
         {
            StopThread();
         }

         DataModule.ClearEvents();
         rwl.AcquireWriterLock(Timeout.Infinite);
         instance = null;
         rwl.ReleaseWriterLock();
         Close();
      }

      /// <summary>
      /// Показать диалог
      /// </summary>
      /// <param name="thread"></param>
      public static void ShowForm(Form parent, Thread thread)
      {
         InitInstance(parent, thread);

         if (thread == null)
            instance.btnCancel.Visible = false;

         instance.Show();         
      }

      private static void InitInstance(Form parent, Thread thread)
      {
         if (instance == null)
         {
            instance = new FmWait(parent);
         }

         instance.thread = thread;
      }

      public static void ShowForm(Form parent, bool makeProgress)
      {
         InitInstance(parent, null);

         if (makeProgress)
         {
            instance.progressTimer = new System.Windows.Forms.Timer();
            instance.progressTimer.Interval = TIMER_INTERVAL;
            instance.progressTimer.Tick += new EventHandler(instance.progressTimer_Tick);
            instance.progress.Maximum = MAX_PROGRESS_VAL;
            instance.progress.Value = START_PROGRESS_VAL;
            instance.progressTimer.Start();
         }

         instance.Show();
      }

      private void progressTimer_Tick(object sender, EventArgs e)
      {
         if (progressPos < MAX_PROGRESS_VAL)
            progressPos++;
         else
            progressPos = START_PROGRESS_VAL;

         progress.Value = progressPos;
         Refresh();
      }

      /// <summary>
      /// Закрыть диалог
      /// </summary>
      public static void CloseForm()
      {
         if (instance != null)
         {
            rwl.AcquireWriterLock(Timeout.Infinite);
            FmWait h = instance;
            instance = null;
            rwl.ReleaseWriterLock();

            h.BeginInvoke(new InvokeDelegate(delegate { h.Close(); }));
         }
      }

      public static void CloseForm(bool terminate)
      {
         if (instance == null)
            return;

         rwl.AcquireWriterLock(Timeout.Infinite);
         FmWait h = instance;
         instance = null;
         rwl.ReleaseWriterLock();


         try
         {
            h.BeginInvoke(new InvokeDelegate(delegate { h.Close(); }));
         }
         catch
         {
         }

         if (terminate)
            h.StopThread();
         else
            h.thread = null;
      }

      public void StopThread()
      {
         if (thread != null)
         {
            thread.Abort();
            thread = null;
         }
      }

      static PI indicator = new PI();
      class PI : IProgress
      {
         #region Члены IProgress

         public void SetText(string text)
         {
            if (instance != null)
            {
               instance.textLabel.BeginInvoke(new InvokeDelegate(delegate
               {
                  if (instance != null)
                     instance.textLabel.Text = text;
               }));
            }
         }

         public void SetMax(int max)
         {
            if (instance != null)
            {
               instance.progress.BeginInvoke(new InvokeDelegate(delegate
               {
                  if (instance != null)
                     instance.progress.Maximum = max;
               }));
            }
         }

         public void AdvancePos(int pos)
         {
            if (instance != null)
            {
               instance.progress.BeginInvoke(new InvokeDelegate(delegate
               {
                  if (instance != null)
                     instance.progress.Value += pos;
               }));
            }
         }

         #endregion

      }

      static public IProgress ProgressIndicator
      {
         get { return indicator; }
      }
   }
}