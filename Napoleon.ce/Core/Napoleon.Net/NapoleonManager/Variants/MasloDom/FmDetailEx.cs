using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
  [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public
   class FmDetailEx : FmDetail
   {

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
      }

      protected override bool IsDocCompleted(DateTime date, GRSoft.Network.DataObject dataObject)
      {
         if (dataObject.GetType() == typeof(ScriptDoc) &&
            ((ScriptDoc)dataObject).org != null && 
            ((ScriptDoc)dataObject).org.GetType() == typeof(PotenzialOrg))
         {
            ScriptDoc sd = dataObject as ScriptDoc;
            if (sd != null)
            {
               ScriptDef def = null;
               if (dsScriptDef.ContainsKey(sd.scriptId))
                  def = dsScriptDef[sd.scriptId];

               if (def != null)
               {
                  int cnt = 0;

                  foreach (ScriptDefItem sdi in def.items)
                     if (!sdi.curType.Equals(Order.OBJECT_NAME))
                        cnt++;

                  return cnt == sd.items.Count;
               }
               return true;
            }
            return true;
         }else
            return base.IsDocCompleted(date, dataObject);
      }
   }
}
