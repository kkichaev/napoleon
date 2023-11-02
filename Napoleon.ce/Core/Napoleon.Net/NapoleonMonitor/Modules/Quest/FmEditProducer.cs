using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmEditProducer : Form
   {
      public FmEditProducer()
      {
         InitializeComponent();
      }

      public static Producer Open(Producer producer)
      {
         Producer result = null;
         FmEditProducer instance = new FmEditProducer();

         if (producer != null)
            instance.tbName.Text = producer.name;

         if (instance.ShowDialog() == DialogResult.OK)
         {
            if (producer == null)
            {
               result = new Producer();
               result.id = System.Guid.NewGuid().ToString().Replace("-", ""); ;
            }
            else
               result = producer;

            result.name = instance.tbName.Text;
         }

         return result;
      }
   }
}
