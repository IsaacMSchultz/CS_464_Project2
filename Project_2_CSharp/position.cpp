
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from position.idl using "rtiddsgen".
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

#include "position.h"

/* ========================================================================= */
Position::Position() {
    timestamp = "";
    route = "";
    vehicle = "";
    stopNumber = 0;
    numStops = 0;
    timeBetweenStops = 0;
    trafficConditions = "";
    fillInRatio = 0;
}

void Position::clear(){
    timestamp = "";
    route = "";
    vehicle = "";
    stopNumber = 0;
    numStops = 0;
    timeBetweenStops = 0;
    trafficConditions = "";
    fillInRatio = 0;
}

System::Boolean Position::copy_from(Position^ src) {

    Position^ dst = this;

    dst->timestamp = src->timestamp;
    dst->route = src->route;
    dst->vehicle = src->vehicle;
    dst->stopNumber = src->stopNumber;
    dst->numStops = src->numStops;
    dst->timeBetweenStops = src->timeBetweenStops;
    dst->trafficConditions = src->trafficConditions;
    dst->fillInRatio = src->fillInRatio;
    return true;
}

Boolean Position::Equals(Object^ other) {
    if (other == nullptr) {
        return false;
    }        
    if (this == other) {
        return true;
    }
    Position^ otherObj =
    dynamic_cast<Position^>(other);
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
    if (numStops != otherObj->numStops) {
        return false;
    }
    if (timeBetweenStops != otherObj->timeBetweenStops) {
        return false;
    }
    if (!trafficConditions->Equals(otherObj->trafficConditions)) {
        return false;
    }
    if (fillInRatio != otherObj->fillInRatio) {
        return false;
    }
    return true;
}

DDS::TypeCode^ Position::get_typecode() {
    if (_typecode == nullptr) {
        _typecode = gcnew DDS::TypeCode(Position_get_typecode());
    }
    return _typecode;
}

DDS_TypeCode* Position_get_typecode()
{
    static RTIBool is_initialized = RTI_FALSE;

    static DDS_TypeCode Position_g_tc_timestamp_string = DDS_INITIALIZE_STRING_TYPECODE((255));
    static DDS_TypeCode Position_g_tc_route_string = DDS_INITIALIZE_STRING_TYPECODE((255));
    static DDS_TypeCode Position_g_tc_vehicle_string = DDS_INITIALIZE_STRING_TYPECODE((255));
    static DDS_TypeCode Position_g_tc_trafficConditions_string = DDS_INITIALIZE_STRING_TYPECODE((255));
    static DDS_TypeCode_Member Position_g_tc_members[8]=
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
        }, 
        {
            (char *)"numStops",/* Member name */
            {
                4,/* Representation ID */          
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
            (char *)"timeBetweenStops",/* Member name */
            {
                5,/* Representation ID */          
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
            (char *)"trafficConditions",/* Member name */
            {
                6,/* Representation ID */          
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
            (char *)"fillInRatio",/* Member name */
            {
                7,/* Representation ID */          
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

    static DDS_TypeCode Position_g_tc =
    {{
            DDS_TK_STRUCT,/* Kind */
            DDS_BOOLEAN_FALSE, /* Ignored */
            -1, /*Ignored*/
            (char *)"Position", /* Name */
            NULL, /* Ignored */      
            0, /* Ignored */
            0, /* Ignored */
            NULL, /* Ignored */
            8, /* Number of members */
            Position_g_tc_members, /* Members */
            DDS_VM_NONE  /* Ignored */         
        }}; /* Type code for Position*/

    if (is_initialized) {
        return &Position_g_tc;
    }

    Position_g_tc_members[0]._representation._typeCode = (RTICdrTypeCode *)&Position_g_tc_timestamp_string;

    Position_g_tc_members[1]._representation._typeCode = (RTICdrTypeCode *)&Position_g_tc_route_string;

    Position_g_tc_members[2]._representation._typeCode = (RTICdrTypeCode *)&Position_g_tc_vehicle_string;

    Position_g_tc_members[3]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_long;

    Position_g_tc_members[4]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_long;

    Position_g_tc_members[5]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_long;

    Position_g_tc_members[6]._representation._typeCode = (RTICdrTypeCode *)&Position_g_tc_trafficConditions_string;

    Position_g_tc_members[7]._representation._typeCode = (RTICdrTypeCode *)&DDS_g_tc_long;

    is_initialized = RTI_TRUE;

    return &Position_g_tc;
}
