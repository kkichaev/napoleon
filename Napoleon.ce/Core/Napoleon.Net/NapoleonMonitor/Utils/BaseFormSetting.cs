using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;

namespace GRSoft.NapoleonManager
{
   [Serializable]
   public abstract class BaseFormSetting<SettingType>
   {
      public static SettingType Load()
      {
         SettingType result = default(SettingType);
         SettingType instance = 
            (SettingType)Activator.CreateInstance(typeof(SettingType));

         if (File.Exists(FileName(typeof(SettingType))))
         {
            Stream stream = File.Open(FileName(typeof(SettingType)), FileMode.Open);
            try
            {
               result = (SettingType)new BinaryFormatter().Deserialize(stream);
            } catch(Exception)
            {

            }
            finally
            {
               stream.Close();
            }
         }

         return result != null ? result : (SettingType)instance;
      }

      public virtual void Save()
      {
         Stream stream = File.Open(FileName(this.GetType()), FileMode.OpenOrCreate);
         try
         {
            new BinaryFormatter().Serialize(stream, this);
         }
         finally
         {
            stream.Close();
         }
      }

      protected static string FileName(Type type) 
      {
         return Config.GetAppHomeDir() + "//" + type.Name; 
      }
   }
}
