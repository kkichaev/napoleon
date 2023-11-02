/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Отчет по школе
 * 
 * kki   12/01/2011   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using GRSoft.NapoleonManager.DataObjects;
using System.IO;

namespace GRSoft.NapoleonManager.Reports
{
   class SchoolActivityReport
   {
      private StringBuilder context = new StringBuilder();

      public SchoolActivityReport()
      {
      }

      public void Build(DataGridView data, DsStudent dsStudent)
      {
         const string DELIM_STR = " ";
         const string EOL_STR = "\n";

         context.Length = 0;   

         foreach (DataGridViewRow row in data.Rows)
         {
            foreach (DataGridViewCell cell in row.Cells)
               context.Append(cell.Value.ToString()).Append(DELIM_STR);

            Lesson lesson = (Lesson)row.Tag;

            foreach (LessonItem item in lesson.items)
            { 
               string studentName = dsStudent.ContainsKey(item.studentID) 
                  ? dsStudent[item.studentID].name
                  : string.Empty;

               context.Append(studentName).Append(DELIM_STR);
               context.Append(item.mark).Append(DELIM_STR);
               context.Append(item.behavior).Append(DELIM_STR);
               context.Append(item.remark).Append(DELIM_STR);
            }

            context.Append(EOL_STR);
         }
      }

      public void Save(string fileName)
      {
         using (StreamWriter outfile = new StreamWriter(fileName))
         {
            outfile.Write(context.ToString());
            outfile.Flush();
            outfile.Close();
         }
      }
   }
}
