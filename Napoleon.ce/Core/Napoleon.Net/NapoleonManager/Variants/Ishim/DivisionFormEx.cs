/*
 * Copyright (C), 2010 - 2011, Гильдия Разработчиков
 *
 * Подразделения для Закромов - выбор склада
 * 
 * ert   28/03/2011   creating
 */
using GRSoft.Network;
using System.Windows.Forms;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System;

namespace GRSoft.NapoleonManager
{
   class DivisionFormEx : DivisionForm
   {
      DataGridViewTextBoxColumn whColumn = new DataGridViewTextBoxColumn();

      public DivisionFormEx()
         : base()
      {
         whColumn.DataPropertyName = "OffTakeCoef";
         whColumn.HeaderText = "Коэф.";
         whColumn.Name = "oftcoef";

         childUserList.Columns.Add(whColumn);
      }

      protected override DivisionForm.DataItem CreateItem(Agent a, DivisionForm form)
      {
         return new DataItemEx(a, form);
      }

      class DataItemEx : DivisionForm.DataItem
      {
         public DataItemEx(Agent a, DivisionForm o)
            : base(a, o)
         {
         }

         public string OffTakeCoef
         {
            get
            {
               string coef = "1.5";
               if (agent != null)
               {
                  CommonConfig cc = ConfigUtils.GetConfig(owner.dsCommonConfig, new ConfigKeyItems("OffTakeCoef"), agent.id);
                  if (cc != null)
                  {
                     coef = cc.value;
                  }
               }

               return coef;
            }

            set
            {
               if (agent != null)
               {
                  CommonConfig cc = ConfigUtils.GetConfig(owner.dsCommonConfig, new ConfigKeyItems("OffTakeCoef"), agent.id);
                  if (cc == null)
                  {
                     cc = new CommonConfig();
                     cc.userid = agent.id;
                     cc.key = "OffTakeCoef";
                     owner.dsCommonConfig.Add(owner.dsCommonConfig.Count, cc);
                  }
                  cc.value = value;
                  owner.parent.AddWriteSet(owner.dsCommonConfig);
               }
            }
         }
      }
   }
}