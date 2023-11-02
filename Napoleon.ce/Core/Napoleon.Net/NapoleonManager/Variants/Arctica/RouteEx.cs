

using GRSoft.NapoleonManager.Utils;
using System.Collections;
using System.Collections.Generic;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public class RouteEx : Route
   {
      public RouteEx()
      {
         DataGridViewTextBoxColumn clmn1 = new DataGridViewTextBoxColumn();
         clmn1.HeaderText = "Менеджер";
         clmn1.DataPropertyName = "Manager";
         clmn1.Width = 50;
         dgvOrgs.Columns.Insert(2, clmn1);

         clmn1 = new DataGridViewTextBoxColumn();
         clmn1.HeaderText = "Направление";
         clmn1.DataPropertyName = "Direction";
         dgvOrgs.Columns.Insert(3, clmn1);

      }
   }

   public partial class OrgRouteQueueItem
   {
      public string Manager { get { return org.org == null ? "" : org.org.manager; } }
      public string Direction { get { return org.org == null ? "" : org.org.direction; } }

   }

   public class FmSelectContrAgentEx : FmSelectContrAgent
   {
      public FmSelectContrAgentEx()
      {
         DataGridViewTextBoxColumn clmn1 = new DataGridViewTextBoxColumn();
         clmn1.HeaderText = "Менеджер";
         clmn1.DataPropertyName = "Manager";
         clmn1.Width = 50;
         dgvOrgs.Columns.Insert(1, clmn1);

         clmn1 = new DataGridViewTextBoxColumn();
         clmn1.HeaderText = "Направление";
         clmn1.DataPropertyName = "Direction";
         dgvOrgs.Columns.Insert(2, clmn1);
      }
   }
}