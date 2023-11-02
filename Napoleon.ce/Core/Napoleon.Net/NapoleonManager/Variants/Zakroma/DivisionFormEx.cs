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
using System.Text;

namespace GRSoft.NapoleonManager
{
   class DivisionFormEx : DivisionForm
   {
      DataSet<int, WHouses> dsWH = null;
      DataGridViewButtonColumn whColumn = new DataGridViewButtonColumn();

      public DivisionFormEx() : base()
      {
         childUserList.CurrentCellDirtyStateChanged += new System.EventHandler(CurrentCellDirtyStateChanged);

         whColumn.DataPropertyName = "WHCode";
         whColumn.HeaderText = "Склад";
         whColumn.Name = "whcode";
         whColumn.AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
             
         childUserList.Columns.Add(whColumn);

         childUserList.CellContentClick += childUserList_CellContentClick;
      }

      void childUserList_CellContentClick(object sender, DataGridViewCellEventArgs e)
      {
         if (e.ColumnIndex == 2)
         {
            DataItemEx d = ((DataGridView)sender).Rows[e.RowIndex].DataBoundItem as DataItemEx;

            if(d != null)
            {
               List<WHouses> list = new List<WHouses>();
               list.AddRange(dsWH.Values);
               FmSelWh fm = new FmSelWh();
               fm.List = list;
               fm.Selected = d.WHCode;

               if (fm.ShowDialog() == DialogResult.OK)
               {
                  d.WHCode = fm.Selected;
                  ((DataGridView)sender).Invalidate();
               }
            }
         }
      }

      void CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         DataGridViewCell cell = childUserList.CurrentCell;
         if (cell != null && childUserList.Columns[cell.ColumnIndex].HeaderText == whColumn.HeaderText)
         {
            childUserList.CommitEdit(DataGridViewDataErrorContexts.Commit);
         }
      }

      internal override void BeforeUpdate(List<IDataSet> updSet)
      {
         IDataSet ds = DataModule.Get(WHouses.OBJECT_NAME);

         if (ds == null) dsWH = new DataSet<int, WHouses>(WHouses.OBJECT_NAME);
         else dsWH = (DataSet<int, WHouses>)ds;

         if (dsWH.Count == 0)
            updSet.Add(dsWH);
      }

      internal override void DataLoaded()
      {
         //whColumn.Items.Clear();
         //foreach (WHouses wh in dsWH.Data)
         //   whColumn.Items.AddRange(wh.name);
      }

      protected override DivisionForm.DataItem CreateItem(Agent a, DivisionForm form)
      {
         return new DataItemEx(a, form);
      }

      class DataItemEx : DivisionForm.DataItem
      {
         public DataItemEx(Agent a, DivisionForm o) : base(a, o)
         {
         }

         public string WHCode
         {
            get
            {
               StringBuilder ret = new StringBuilder();
               if (agent != null)
               {
                  DivisionFormEx o = owner as DivisionFormEx;

                  foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
                     if (serverConfig.userid.Equals(agent.id) && serverConfig.key.Equals("WHouse"))
                     {
                        string[] v = serverConfig.value.Split(';');

                        for (int i = 0; i < v.Length; i++ )
                        {
                           int whn = int.Parse(v[i]);
                           if (o.dsWH.ContainsKey(whn))
                           {
                              if (ret.Length > 0)
                                 ret.Append(";");

                              ret.Append(o.dsWH[whn].name);
                           }
                        }
                     }
               }

               return ret.ToString();
            }

            set
            {
               DivisionFormEx o = owner as DivisionFormEx;
               if (agent != null)
               {
                  string[] vid = value.Split(';');
                  Dictionary<string, int> map = new Dictionary<string, int>();
                  
                  foreach (WHouses wh in o.dsWH.Data)
                     if(!map.ContainsKey(wh.name))
                        map.Add(wh.name, wh.id);

                  StringBuilder id = new StringBuilder();
                  for (int i = 0; i < vid.Length; i++)
                  {
                     if (map.ContainsKey((vid[i])))
                     {
                        int w = map[vid[i]];

                        if(id.Length > 0)
                           id.Append(";");

                        id.Append(w);
                     }
                  }

                  if (id.Length == 0)
                     return;

                  DataSet<int, CommonConfig> addCfg = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
                  CommonConfig cfg = new CommonConfig();
                  cfg.key = "WHouse";
                  cfg.userid = agent.id;
                  cfg.value = id.ToString();

                  addCfg.Add(0, cfg);
                  List<IDataSet> wr = new List<IDataSet>();
                  wr.Add(addCfg);

                  if (DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection()))
                  {
                     bool finded = false;
                     foreach (CommonConfig serverConfig in o.dsCommonConfig.Data)
                     {
                        if (serverConfig.userid.Equals(agent.id) && serverConfig.key.Equals("WHouse"))
                        {
                           serverConfig.value = cfg.value;
                           finded = true;
                           break;
                        }
                     }

                     if (!finded)
                        o.dsCommonConfig.Add(o.dsCommonConfig.Count, cfg);
                  }
               }
            }
         }
      }
   }

   public class WHouses : GRSoft.Network.DataObject
   {
      public static string OBJECT_NAME = "WHouses";

      [KeyField]
      public int id = 0;
      public string name = "";

      public override string ToString()
      {
         return name;
      }
   }
}