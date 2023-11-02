using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Drawing;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class UserFormEx : UserForm
   {
      public static readonly string CHECK_FOCUSED_ITEMS_KEY = "CheckFocusedItems";

      CheckBox cbCheckFocusItems;
      public UserFormEx(Divisions owner):base(owner)
      {
         cbCheckFocusItems = new CheckBox();
         udScript.Controls.Add(cbCheckFocusItems);

         cbCheckFocusItems.Name = "btnEditRoute";
         cbCheckFocusItems.Location = new System.Drawing.Point(2, 6);
         cbCheckFocusItems.Size = new System.Drawing.Size(Size.Width - 4, 18);
         cbCheckFocusItems.Anchor = AnchorStyles.Left | AnchorStyles.Right | AnchorStyles.Top;
         cbCheckFocusItems.TabIndex = 1;
         cbCheckFocusItems.Text = "Проверка фокусного товара в заявках";
         cbCheckFocusItems.CheckStateChanged += new EventHandler(cbCheckFocusItems_CheckStateChanged);

         Size sz = tvScript.Size;
         Point l = tvScript.Location;
         tvScript.Dock = DockStyle.None;
         tvScript.Size = new Size(sz.Width, sz.Height - 25);
         tvScript.Location = new Point(l.X, l.Y + 25);
         tvScript.Anchor = AnchorStyles.Left | AnchorStyles.Right | AnchorStyles.Bottom | AnchorStyles.Top;

         //ToolStripItem tsi = owner.tb.Items[Divisions.FOCUSED_ITEM_NAME];
         //if (tsi != null)
         //   tsi.Visible = false;
      }

      void cbCheckFocusItems_CheckStateChanged(object sender, EventArgs e)
      {
         DataSet<int, CommonConfig> addCfg = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
         CommonConfig cfg = new CommonConfig();
         cfg.key = CHECK_FOCUSED_ITEMS_KEY;
         cfg.userid = Agent.id;
         cfg.value = (cbCheckFocusItems.Checked) ? "1" : "0";

         addCfg.Add(0, cfg);
         List<IDataSet> wr = new List<IDataSet>();
         wr.Add(addCfg);

         if (DataModule.UpdateDataSet(wr, null, null, Config.GetConfig().GetConnection()))
         {
            bool finded = false;
            foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
            {
               if (serverConfig.userid.Equals(Agent.id) && serverConfig.key.Equals(cfg.key))
               {
                  serverConfig.value = cfg.value;
                  finded = true;
                  break;
               }
            }

            if (!finded)
               owner.dsCommonConfig.Add(owner.dsCommonConfig.Count, cfg);
         }
      }

      public override Agent Agent
      {
         get
         {
            return base.Agent;
         }
         set
         {
            base.Agent = value;

            cbCheckFocusItems.Checked = false;
            foreach (CommonConfig serverConfig in owner.dsCommonConfig.Data)
               if (serverConfig.userid.Equals(value.id) && serverConfig.key.Equals(CHECK_FOCUSED_ITEMS_KEY))
               {
                  int val = 0;
                  int.TryParse(serverConfig.value, out val);
                  cbCheckFocusItems.Checked = (val > 0);

                  break;
               }
         }
      }
   }
}
