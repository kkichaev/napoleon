using System;
using System.Collections.Generic;
using System.Text;
using System.Data.SQLite;
using System.IO;
using System.Data.Common;
//using Community.CsharpSqlite.SQLiteClient;

namespace GRSoft.Ads
{
   class LocalDataBase
   {
      private static LocalDataBase instance;

      public static LocalDataBase Instance()
      {
         if (instance == null)
            instance = new LocalDataBase();

         return instance;
      }

      public DbParameter CreateParameter(string name, object value)
      {
         if (name[0] != '@')
            name = "@" + name;
         //return new SqliteParameter(name, value);
         return new SQLiteParameter(name, value);
      }

      public DbConnection Connection
      {
         get
         {
            const string LOCAL_BASE = "local.db";
            string file = Config.GetAppHomeDir() + LOCAL_BASE;

            bool createBase = false;

            //if (!File.Exists(file))
            //   createBase = true;
            //DbConnection result = new SqliteConnection();
            //result.ConnectionString = string.Format("Version=3,uri=file:{0}", file);
            //if (createBase)
            //{
            //   result.Open();
            //   DbCommand createDataBase = result.CreateCommand();
            //   createDataBase.CommandText = Ads.Properties.Resources.CreateLocalDatabase;
            //   createDataBase.ExecuteNonQuery();
            //   result.Close();
            //}


            if (!File.Exists(file))
            {
               SQLiteConnection.CreateFile(file);
               createBase = true;
            }

            SQLiteConnection result = new SQLiteConnection();
            SQLiteConnectionStringBuilder conString = new SQLiteConnectionStringBuilder();
            conString.DataSource = file;
            result.ConnectionString = conString.ToString();

            if (createBase)
            {
               result.Open();
               SQLiteCommand createDataBase = result.CreateCommand();
               createDataBase.CommandText = Ads.Properties.Resources.CreateLocalDatabase;
               createDataBase.ExecuteNonQuery();
               result.Close();
            }

            return result;
         }
      }
   }
}
