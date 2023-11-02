/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма подразделений
 * 
 * ert   03/05/2010   creating
 */

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using System.Globalization;
using System.Collections;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
    public partial class Divisions : Form
    {
        public Divisions()
        {
            InitializeComponent();
            __Initing();

            setColor.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            setColor.Image = Properties.Resources.colorize;

            btnQuestion.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            btnQuestion.Image = Properties.Resources.quest_doc;
        }

      private void btnPriceRemnants_Click(object sender, EventArgs e)
      {
#if PRICE_PHOTO_VIEW
         Type prcType = FormEntries.GetFormType(typeof(FmPricePhoto));
#else
         Type prcType = FormEntries.GetFormType(typeof(FmPrice));
#endif
         ConstructorInfo ci = prcType.GetConstructor(Type.EmptyTypes);
         Form fm = (Form)ci.Invoke(new object[] { });
         fm.Show();
      }
   }
}
