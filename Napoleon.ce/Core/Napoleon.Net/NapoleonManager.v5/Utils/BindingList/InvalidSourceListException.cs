using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager.Utils
{
    [Serializable]
    public class InvalidSourceListException : Exception
    {
       public InvalidSourceListException()
          : base("InvalidSourceListException")
       { 
       }

        public InvalidSourceListException(string message)
            : base(message)
        {

        }

        public InvalidSourceListException(System.Runtime.Serialization.SerializationInfo info, System.Runtime.Serialization.StreamingContext context)
            : base(info, context)
        {

        }
    }
}
