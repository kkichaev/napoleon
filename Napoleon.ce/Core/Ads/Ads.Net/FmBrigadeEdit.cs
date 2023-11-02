using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.Ads
{
   public partial class FmBrigadeEdit : Form
   {
      public FmBrigadeEdit()
      {
         InitializeComponent();
         ToolTip tooltip = new ToolTip();
         tooltip.SetToolTip(btnJobType, "Щелкните для выбра вида работ.");
      }

      public static bool ShowInstance(Brigade brigade)
      {
         bool addMode = brigade == null;
         FmBrigadeEdit instance = new FmBrigadeEdit();

         if (brigade != null)
         {
            instance.tbLogin.Text = brigade.Login;
            instance.tbName.Text = brigade.Name;
            instance.tbPassword.Text = brigade.Password;
            instance.tbJobType.Tag = brigade.JobType;
            instance.tbJobType.Text = brigade.JobTypeText;
            instance.tbJobType.ForeColor = brigade.JobTypeColor;
            instance.tbPrefix.Text = brigade.prefix;
         }

         if (addMode)
            instance.Text = "Добавить";
         else
            instance.Text = "Изменить";

         if (instance.ShowDialog() == DialogResult.OK)
         {
            DsBrigade dsBrigade = new DsBrigade(false); 
            Brigade newBrigade = brigade ?? new Brigade();
            
            if (addMode)
               newBrigade.id = ((DsBrigade)DataModule.Get(Brigade.OBJECT_NAME)).GetNextKey();

            newBrigade.login = instance.tbLogin.Text;
            newBrigade.password = instance.tbPassword.Text;
            newBrigade.name = instance.tbName.Text;
            newBrigade.JobType = (JobType)instance.tbJobType.Tag;
            newBrigade.prefix = instance.tbPrefix.Text;

            dsBrigade.Add(newBrigade.id, newBrigade);

            List<IDataSet> list = new List<IDataSet>();
            list.Add(dsBrigade);

            DsDivision dsDivision = (DsDivision)DataModule.Get(Division.OBJECT_NAME);

            if (dsDivision != null && 
               dsDivision.Count > 0)
            {
               Division.DivisionAgent agent = new Division.DivisionAgent();
               agent.brigade = newBrigade;
               agent.id = newBrigade.id;

               foreach (Division division in dsDivision.Data)
               {
                  if (division.name.Equals(Config.GetConfig().division))
                  {
                     division.agents.Add(agent);
                     list.Add(dsDivision);
                     break;
                  }
               }
            }
   
            if (DataModule.UpdateDataSet(list, null, null, Config.GetConfig().GetConnection()))
            {
               return true;
            }
            else MessageBox.Show("Ошибка при добавлении");
         }

         return false;
      }

      private void btnJobType_Click(object sender, EventArgs e)
      {
         FmJobsType.ShowInstance((JobType)tbJobType.Tag,  
            new Invoker(delegate(object param)
         {
            if (param != null)
            {
               tbJobType.Text = ((JobType)param).Name;
               tbJobType.ForeColor = ((JobType)param).Color;
               tbJobType.Tag = param;
            }
         }));
      }
   }
}
