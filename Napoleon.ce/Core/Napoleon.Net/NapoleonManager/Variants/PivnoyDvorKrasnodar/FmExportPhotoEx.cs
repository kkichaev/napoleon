using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmExportPhotoEx : FmExportPhoto
   {
      SimpleDataSet<ScriptDef> dsScriptDef;
      SimpleDataSet<ScriptDoc> dsScripts;
      Dictionary<int, ScriptDef> scriptDefs = new Dictionary<int, ScriptDef>();

      public FmExportPhotoEx()
      {
         dsScriptDef = new SimpleDataSet<ScriptDef>(ScriptDef.OBJECT_NAME, false);
         dsScripts = new SimpleDataSet<ScriptDoc>(ScriptDoc.OBJECT_NAME, false);   
      }

      string GetScriptStep(Visit v)
      {
         string ret = "";

         foreach (ScriptDoc sd in dsScripts.Data)
         {
            int cnt = 0;
            foreach (ScriptDocItem i in sd.items)
            {
               if (i.type == Visit.OBJECT_NAME && i.date.CompareTo(v.created) == 0)
               {
                  if (scriptDefs.ContainsKey(sd.scriptId))
                  {
                     ScriptDef def = scriptDefs[sd.scriptId];
                     if (def.items.Count > cnt)
                        ret = def.items[cnt].Name;
                  }
                  break;
               }
               cnt++;
            }
         }

         return ret;
      }

      protected override void DoLoadData()
      {
         scriptDefs.Clear();

         foreach (ScriptDef sd in dsScriptDef.Data)
            scriptDefs[sd.id] = sd;

         base.DoLoadData();
      }

      protected override void AddDataSet(List<IDataSet> upd, Agent a, DateTime start, DateTime finish)
      {
         base.AddDataSet(upd, a, start, finish);
         dsScripts.Filter = String.Format(COMMON_FILTER_STR, "created", start, start.AddDays(1), a.id);
         dsScriptDef.Filter = "\"userid\" is null or not \"userid\" is null";

         upd.Add(dsScriptDef);
         upd.Add(dsScripts);
      }

      protected override string FileName(string saveName, Visit v, int cnt, string dir)
      {
         string ss = GetScriptStep(v);

         return string.Format(saveName, dir, WinChar(ss), WinChar(v.OrgName), WinChar(v.OrgAddr), WinChar(v.Created.ToString()), cnt);
      }

      protected override string GetFileNameMask()
      {
         return @"{0}\{1}_{2}_{3}_{4}({5}).jpg";
      }

      protected override string GetPhotoText(BaseDocument doc)
      {
         string ss = doc is Visit ? GetScriptStep((Visit)doc) : string.Empty;
         return ss + " " + base.GetPhotoText(doc);
      }
   }
}
