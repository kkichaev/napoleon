//
//  objects.h
//  NapoleonCore
//
//  Created by Denis Mosyagin on 30.08.2021.
//

@interface Agent : GRSObject
@property NSString* id;
@property NSString* name;
@property NSString* login;
@property NSString* password;
@end

@interface DivisionAgent : GRSObject
@property NSString* id;
@end

@interface Division : GRSObject
@property int id;
@property int parent;
@property NSString* name;
@property NSString* descr;
@property NSMutableArray<DivisionAgent*>* agents;
@end
