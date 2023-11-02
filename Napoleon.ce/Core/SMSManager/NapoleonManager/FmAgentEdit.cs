/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Форма Редактирование Агента
 * 
 * kki   23/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.DataObjects;
using GRSoft.NapoleonManager.Utils;

namespace GRSoft.NapoleonManager
{
   public partial class FmAgentEdit : Form
   {
      private Agent agent;

      public FmAgentEdit(Agent agent)
      {
         InitializeComponent();
         this.agent = agent;
         Init();
      }

      private void Init()
      {
         if (agent.id.Length == 0)
            agent.id = Convert.ToString(DateTime.Now.Ticks);
         tbLogin.Text = agent.login;
         tbName.Text = agent.Name;
         tbPassw.Text = agent.password;
         tbPhone.Text = agent.phone;
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         agent.login = tbLogin.Text;
         agent.name = tbName.Text;
         agent.password = tbPassw.Text;
         agent.phone = tbPhone.Text;
      }

      private void FmAgentEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK &&
            (tbPassw.Text.Length == 0 || tbLogin.Text.Length == 0))
         {
            e.Cancel = true;
            Dialogs.PleaseFillFieldsDlg();
         }
      }
   }
}