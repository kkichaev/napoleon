using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      Dictionary<DateTime, ScriptDoc> scriptDocs = new Dictionary<DateTime, ScriptDoc>();
      List<DateTime> visitDoc = new List<DateTime>();

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
      }

      protected override void CellFormatting(DataGridViewCellFormattingEventArgs e)
      {
         base.CellFormatting(e);

         if (e.RowIndex < 0 || e.RowIndex >= dgvDetail.Rows.Count || refreshing)
            return;

         GRSoft.Network.DataObject dataObject = (dgvDetail.Rows[e.RowIndex].DataBoundItem as OrderDetailRepresentation).StoreObject;
         BaseDocument bd = dataObject as BaseDocument;

         if (bd != null)
         {

            if (visitDoc.Contains(bd.created))
               e.CellStyle.BackColor = Color.Red;
            else
            {
               if (!(bd is ScriptDoc))
               {
                  if (scriptDocs.ContainsKey(bd.created))
                     bd = scriptDocs[bd.created];
                  else
                     bd = null;
               }

               if (bd is ScriptDoc && config.scriptWorkMinTime > 0)
               {
                  ScriptDoc sd = (ScriptDoc)bd;

                  TimeSpan ts = sd.End - sd.Start;

                  if (ts.TotalMinutes < config.scriptWorkMinTime)
                     e.CellStyle.BackColor = Color.Red;
               }
            }
         }
      }

      protected override void AfterRefreshData()
      {
         base.AfterRefreshData();

         scriptDocs.Clear();

         foreach(ScriptDoc s in dsScriptDoc.Values)
         {
            foreach (ScriptDocItem d in s.items)
            {
               if (d.Document is BaseDocument)
               {
                  scriptDocs[((BaseDocument)d.Document).created] = s;
               }
            }
         }
      }

      protected override void ReloadData()
      {
         base.ReloadData();

         DateTime dt = DateTime.MinValue;
         string id = string.Empty;

         if (config.visitBreakeMaxTime > 0)
         {
            foreach (OrderDetailRepresentation odr in oDetail)
            {
               BaseDocument bd = odr.dataObject as BaseDocument;

               if (bd == null)
                  continue;

               if (id == string.Empty)
               {
                  id = bd.id;
                  dt = bd.created;
               }
               else
               {
                  if (!id.Equals(bd.id))
                  {
                     id = bd.id;

                     TimeSpan df = bd.created - dt;
                     
                     if (bd.created.Date == dt.Date &&  df.TotalMinutes > config.visitBreakeMaxTime)
                     {
                        visitDoc.Add(bd.created);
                     }

                     dt = bd.created;
                  }
               }
            }

            dgvDetail.Update();
         }
      }
   }
}
