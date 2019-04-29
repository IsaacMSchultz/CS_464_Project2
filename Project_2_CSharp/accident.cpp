
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from accident.idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

#pragma unmanaged
#include "ndds/ndds_cpp.h"
#pragma managed

using namespace System;
using namespace System::Collections;
using namespace DDS;

#include "accident.h"

/* ========================================================================= */
Accident::Accident() {
    timestamp = "";
    route = "";
    vehicle = "";
    stopNumber = 0;
}

void Accident::clear(){
    timestamp = "";
    route = "";
    vehicle = "";
    stopNumber = 0;
}

System::Boolean Accident::copy_from(Accident^ src) {

    Accident^ dst = this;

    dst->timestamp = src->timestamp;
    dst->route = src->route;
    dst->vehicle = src->vehicle;
    dst->stopNumber = src->stopNumber;
    return true;
}

Boolean Accident::Equals(Object^ other) {
    if (other == nullptr) {
        return false;
    }        
    if (this == other) {
        return true;
    }
    Accident^ otherObj =
    dynamic_cast<Accident^>(other);
    if (otherObj == nullptr) {
        return false;
    }

    if (!timestamp->Equals(otherObj->timestamp)) {
        return false;
    }
    if (!route->Equals(otherObj->route)) {
        return false;
    }
    if (!vehicle->Equals(otherObj->vehicle)) {
        return false;
    }
    if (stopNumber != otherObj->stopNumber) {
        return false;
    }
    return true;
}

DDS::TypeCode^ Accident::get_typecode() {
    if (_typecode == nullptr) {
        _typecode = gcnew DDS::TypeCode(Accident_get_typecode());
    }
    return _typecode;
}

DDS_TypeCode* Accident_get_typecode()
{
    static RTIBool is_initialized = RTI_FALSE;

    static DDS_TypeCode Accident_g_tc_timestamp_string = DDS_INITIALIZE_STRING_TYPECODE((255));
    static DDS_TypeCode Accident_g_tc_route_string = DDS_INITIALIZE_STRING_TYPECODE((255));
    static DDS_TypeCode Accident_g_tc_vehicle_string = DDS_INITIALIZE_STRING_TYPECODE((255));
    static DDS_TypeCode_Member Accident_g_tc_members[4]=
    {

        {
            (char *)"timestamp",/* Member name */
            {
                0,/* Representation ID */          
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            RTI_CDR_REQUIRED_MEMBER, /* Is a key? */
            DDS_PUBLIC_MEMBER,/* Member visibility */
            1,
            NULL/* Ignored */
        }, 
        {
            (char *)"route",/* Member name */
            {
                1,/* Representation ID */          
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            RTI_CDR_REQUIRED_MEMBER, /* Is a key? */
            DDS_PUBLIC_MEMBER,/* Member visibility */
            1,
            NULL/* Ignored */
        }, 
        {
            (char *)"vehicle",/* Member name */
            {
                2,/* Representation ID */          
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            RTI_CDR_REQUIRED_MEMBER, /* Is a key? */
            DDS_PUBLIC_MEMBER,/* Member visibility */
            1,
            NULL/* Ignored */
        }, 
        {
            (char *)"stopNumber",/* Member name */
            {
                3,/* Representation ID */          
                DDS_BOOLEAN_FALSE,/* Is a pointer? */
                -1, /* Bitfield bits */
                NULL/* Member type code is assigned later */
            },
            0, /* Ignored */
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            RTI_CDR_REQUIRED_MEMBER, /* Is a key? */
            DDS_PUBLIC_MEMBER,/* Member visibility */
            1,
            NULL/* Ignored */
        }
    };

    static DDS_TypeCode Accident_g_tc =
    {{
            DDS_TK_STRUCT,/* Kind */
            DDS_BOOLEAN_FALSE, /* Ignored */
            -1, /*Ignored*/
            (char *)"Accident", /* Name */
            NULL, /* Ignored */      
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            4, /* Number of members */
            Accident_g_tc_members, /* Members */
            DDS_VM_NONE  /* Ignored */         
        }}; /* Type code for Accident*/

    if (is_initialized) {
        return &Accident_g_tc;
    }

    Accident_g_tc_members[0]._representation._typeCode = (RTICdrTypeCode *)&Accident_g_tc_timestamp_string;

    Accident_g_tc_members[1]._representation._typeCode = (RTICdrTypeCode *)&Accident_g_tc_route_string;

    Accident_g_tc_members[2]._representation._typeCode = (RTICdrTypeCode *)&Accident_g_tc_vehicle_string;

    Accident_g_tc_members[3]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_long;

    is_initialized = RTI_TRUE;

    return &Accident_g_tc;
}
