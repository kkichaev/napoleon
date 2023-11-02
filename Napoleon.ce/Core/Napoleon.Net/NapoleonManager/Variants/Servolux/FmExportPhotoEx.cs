namespace GRSoft.NapoleonManager
{
   public class FmExportPhotoEx : FmExportPhoto
   {
      public FmExportPhotoEx() { }

      protected override string MakeFileName(string dir, BaseDocument doc, string scriptStep, int step)
      {
         return string.Format(@"{0}\{1}{2}{3}{4}({5}).jpg", dir.ToString(), WinChar(doc.OrgName), 
            WinChar(doc.Address), WinChar(doc.Created.ToString("dd.MM.yyyy_HH_mm_ss")),
           "", step);
         //return base.MakeFileName(dir, doc, scriptStep, step);
      }
   }
}