/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Данные, что необходимо сохранять между запусками программы
 * 
 * kki   12/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml.Serialization;
using System.Runtime.Serialization.Formatters.Binary;

namespace GRSoft.NapoleonManager.Utils
{
   [Serializable]
   class PermanentData
   {
      public static readonly int NOT_USED = -1;

      private int localityID = NOT_USED;
      private int schoolID = NOT_USED;
      private int classID = NOT_USED;
      private string agentID = string.Empty;

      static readonly string FILE_NAME = "data.dat";

      private static PermanentData instance;

      public static PermanentData Data
      {
         get
         {
            if (instance == null)
            {
               instance = new PermanentData();
               instance.Load();
            }

            return instance;
         }
      }

      public int LocalityID
      {
         get { return localityID; }
         set { localityID = value; Save(); }
      }

      public int SchoolID
      {
         get { return schoolID; }
         set { schoolID = value; Save(); }
      }

      public int ClassID
      {
         get { return classID; }
         set { classID = value; Save(); }
      }

      public string AgentID
      {
         get { return agentID ?? string.Empty; }
         set { agentID = value; Save(); }
      }

      private void Save()
      {
         BinaryFormatter formatter = new BinaryFormatter();
         Stream stream = new FileStream(FILE_NAME, FileMode.OpenOrCreate, FileAccess.Write);
         formatter.Serialize(stream, this);
         stream.Close();
      }

      private void Load()
      {
         if (!File.Exists(FILE_NAME))
            return;

         try
         {
            BinaryFormatter formatter = new BinaryFormatter();
            Stream stream = new FileStream(FILE_NAME, FileMode.Open, FileAccess.Read);
            instance = (PermanentData)formatter.Deserialize(stream);
            stream.Close();
         }
         catch(Exception)
         {
         }
      }
   }
}
