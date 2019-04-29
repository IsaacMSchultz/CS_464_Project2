/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from position.idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

#include "positionPlugin.h"

#pragma unmanaged
#include "ndds/ndds_cpp.h"
#include "osapi/osapi_utility.h"
#pragma managed

using namespace System::Runtime::InteropServices;

/* ------------------------------------------------------------------------
*  Type Position
* ------------------------------------------------------------------------ */

/* ------------------------------------------------------------------------
Support functions:
* ------------------------------------------------------------------------ */

void 
PositionPlugin::print_data(
    Position^ sample,
    String^ desc,
    UInt32 indent_level) {

    for (UInt32 i = 0; i < indent_level; ++i) { Console::Write("   "); }

    if (desc != nullptr) {
        Console::WriteLine("{0}:", desc);
    } else {
        Console::WriteLine();
    }

    if (sample == nullptr) {
        Console::WriteLine("null");
        return;
    }

    DataPrintUtility::print_object(
        sample->timestamp, "timestamp", indent_level);

    DataPrintUtility::print_object(
        sample->route, "route", indent_level);

    DataPrintUtility::print_object(
        sample->vehicle, "vehicle", indent_level);

    DataPrintUtility::print_object(
        sample->stopNumber, "stopNumber", indent_level);

    DataPrintUtility::print_object(
        sample->numStops, "numStops", indent_level);

    DataPrintUtility::print_object(
        sample->timeBetweenStops, "timeBetweenStops", indent_level);

    DataPrintUtility::print_object(
        sample->trafficConditions, "trafficConditions", indent_level);

    DataPrintUtility::print_object(
        sample->fillInRatio, "fillInRatio", indent_level);

}

/* ------------------------------------------------------------------------
(De)Serialize functions:
* ------------------------------------------------------------------------ */

Boolean 
PositionPlugin::serialize(
    TypePluginDefaultEndpointData^ endpoint_data,
    Position^ sample,
    CdrStream% stream,    
    Boolean serialize_encapsulation,
    UInt16 encapsulation_id,
    Boolean serialize_sample, 
    Object^ endpoint_plugin_qos)
{
    CdrStreamPosition position;

    if (serialize_encapsulation) {
        /* Encapsulate sample */

        if (!stream.serialize_and_set_cdr_encapsulation(encapsulation_id)) {
            return false;
        }

        position = stream.reset_alignment();

    }

    if (serialize_sample) {

        if (!stream.serialize_string(sample->timestamp  , (255))) {
            return false;
        }
        if (!stream.serialize_string(sample->route  , (255))) {
            return false;
        }
        if (!stream.serialize_string(sample->vehicle  , (255))) {
            return false;
        }
        if (!stream.serialize_long(sample->stopNumber )) {
            return false;
        }
        if (!stream.serialize_long(sample->numStops )) {
            return false;
        }
        if (!stream.serialize_long(sample->timeBetweenStops )) {
            return false;
        }
        if (!stream.serialize_string(sample->trafficConditions  , (255))) {
            return false;
        }
        if (!stream.serialize_long(sample->fillInRatio )) {
            return false;
        }

    }

    if(serialize_encapsulation) {
        stream.restore_alignment(position);
    }

    return true;
}

Boolean 
PositionPlugin::deserialize_sample(
    TypePluginDefaultEndpointData^ endpoint_data,
    Position^ sample,
    CdrStream% stream,   
    Boolean deserialize_encapsulation,
    Boolean deserialize_data, 
    Object^ endpoint_plugin_qos)
{
    CdrStreamPosition position;

    if(deserialize_encapsulation) {
        /* Deserialize encapsulation */
        if (!stream.deserialize_and_set_cdr_encapsulation()) {
            return false;
        }

        position = stream.reset_alignment();

    }

    if (deserialize_data) {
        sample->clear();                

        try{

            sample->timestamp = stream.deserialize_string( (255));
            if (sample->timestamp  == nullptr) {
                return false;
            }
            sample->route = stream.deserialize_string( (255));
            if (sample->route  == nullptr) {
                return false;
            }
            sample->vehicle = stream.deserialize_string( (255));
            if (sample->vehicle  == nullptr) {
                return false;
            }
            sample->stopNumber = stream.deserialize_long();
            sample->numStops = stream.deserialize_long();
            sample->timeBetweenStops = stream.deserialize_long();
            sample->trafficConditions = stream.deserialize_string( (255));
            if (sample->trafficConditions  == nullptr) {
                return false;
            }
            sample->fillInRatio = stream.deserialize_long();

        } catch (System::ApplicationException^  e) {
            if (stream.get_remainder() >= RTI_CDR_PARAMETER_HEADER_ALIGNMENT) {
                throw gcnew System::ApplicationException("Error deserializing sample! Remainder: " + stream.get_remainder() + "\n" +
                "Exception caused by: " + e->Message);
            }
        }
    }

    if(deserialize_encapsulation) {
        stream.restore_alignment(position);
    }

    return true;
}

Boolean
PositionPlugin::skip(
    TypePluginDefaultEndpointData^ endpoint_data,
    CdrStream% stream,   
    Boolean skip_encapsulation,
    Boolean skip_sample, 
    Object^ endpoint_plugin_qos)
{
    CdrStreamPosition position;

    if (skip_encapsulation) {
        if (!stream.skip_encapsulation()) {
            return false;
        }

        position = stream.reset_alignment();

    }

    if (skip_sample) {
        if (!stream.skip_string((255))) {
            return false;
        }
        if (!stream.skip_string((255))) {
            return false;
        }
        if (!stream.skip_string((255))) {
            return false;
        }
        if (!stream.skip_long()) {
            return false;
        }
        if (!stream.skip_long()) {
            return false;
        }
        if (!stream.skip_long()) {
            return false;
        }
        if (!stream.skip_string((255))) {
            return false;
        }
        if (!stream.skip_long()) {
            return false;
        }
    }

    if(skip_encapsulation) {
        stream.restore_alignment(position);
    }

    return true;
}

/*
size is the offset from the point where we have do a logical reset
Return difference in size, not the final offset.
*/
UInt32 
PositionPlugin::get_serialized_sample_max_size(
    TypePluginDefaultEndpointData^ endpoint_data,
    Boolean include_encapsulation,
    UInt16 encapsulation_id,
    UInt32 current_alignment)
{

    UInt32 initial_alignment = current_alignment;

    UInt32 encapsulation_size = current_alignment;

    if (include_encapsulation) {
        if (!CdrStream::valid_encapsulation_id(encapsulation_id)) {
            return 1;
        }

        encapsulation_size = CdrSizes::ENCAPSULATION->serialized_size(
            current_alignment);
        encapsulation_size -= current_alignment;
        current_alignment = 0;
        initial_alignment = 0;

    }

    current_alignment +=CdrSizes::STRING->serialized_size(
        current_alignment , (255)+1);

    current_alignment +=CdrSizes::STRING->serialized_size(
        current_alignment , (255)+1);

    current_alignment +=CdrSizes::STRING->serialized_size(
        current_alignment , (255)+1);

    current_alignment +=CdrSizes::LONG->serialized_size(
        current_alignment );

    current_alignment +=CdrSizes::LONG->serialized_size(
        current_alignment );

    current_alignment +=CdrSizes::LONG->serialized_size(
        current_alignment );

    current_alignment +=CdrSizes::STRING->serialized_size(
        current_alignment , (255)+1);

    current_alignment +=CdrSizes::LONG->serialized_size(
        current_alignment );

    if (include_encapsulation) {
        current_alignment += encapsulation_size;
    }

    return  current_alignment - initial_alignment;
}

UInt32
PositionPlugin::get_serialized_sample_min_size(
    TypePluginDefaultEndpointData^ endpoint_data,
    Boolean include_encapsulation,
    UInt16 encapsulation_id,
    UInt32 current_alignment)
{

    UInt32 initial_alignment = current_alignment;

    UInt32 encapsulation_size = current_alignment;

    if (include_encapsulation) {
        if (!CdrStream::valid_encapsulation_id(encapsulation_id)) {
            return 1;
        }

        encapsulation_size = CdrSizes::ENCAPSULATION->serialized_size(
            encapsulation_size);
        current_alignment = 0;
        initial_alignment = 0;

    }

    current_alignment +=CdrSizes::STRING->serialized_size(
        current_alignment, 1);
    current_alignment +=CdrSizes::STRING->serialized_size(
        current_alignment, 1);
    current_alignment +=CdrSizes::STRING->serialized_size(
        current_alignment, 1);
    current_alignment +=CdrSizes::LONG->serialized_size(
        current_alignment);
    current_alignment +=CdrSizes::LONG->serialized_size(
        current_alignment);
    current_alignment +=CdrSizes::LONG->serialized_size(
        current_alignment);
    current_alignment +=CdrSizes::STRING->serialized_size(
        current_alignment, 1);
    current_alignment +=CdrSizes::LONG->serialized_size(
        current_alignment);

    if (include_encapsulation) {
        current_alignment += encapsulation_size;
    }

    return  current_alignment - initial_alignment;
}

UInt32 
PositionPlugin::get_serialized_sample_size(
    TypePluginDefaultEndpointData^ endpoint_data,
    Boolean include_encapsulation,
    UInt16 encapsulation_id,
    UInt32 current_alignment,
    Position^ sample)
{

    UInt32 initial_alignment = current_alignment;

    UInt32 encapsulation_size = current_alignment;

    if (include_encapsulation) {
        if (!CdrStream::valid_encapsulation_id(encapsulation_id)) {
            return 1;
        }

        encapsulation_size = CdrSizes::ENCAPSULATION->serialized_size(
            current_alignment);
        encapsulation_size -= current_alignment;
        current_alignment = 0;
        initial_alignment = 0;
        endpoint_data->set_base_alignment(current_alignment);  
    }

    current_alignment += CdrSizes::STRING->serialized_size(
        endpoint_data->get_alignment(current_alignment), sample->timestamp);

    current_alignment += CdrSizes::STRING->serialized_size(
        endpoint_data->get_alignment(current_alignment), sample->route);

    current_alignment += CdrSizes::STRING->serialized_size(
        endpoint_data->get_alignment(current_alignment), sample->vehicle);

    current_alignment += CdrSizes::LONG->serialized_size(
        endpoint_data->get_alignment(current_alignment));

    current_alignment += CdrSizes::LONG->serialized_size(
        endpoint_data->get_alignment(current_alignment));

    current_alignment += CdrSizes::LONG->serialized_size(
        endpoint_data->get_alignment(current_alignment));

    current_alignment += CdrSizes::STRING->serialized_size(
        endpoint_data->get_alignment(current_alignment), sample->trafficConditions);

    current_alignment += CdrSizes::LONG->serialized_size(
        endpoint_data->get_alignment(current_alignment));
    if (include_encapsulation) {
        current_alignment += encapsulation_size;
    }

    return current_alignment - initial_alignment;
}

UInt32
PositionPlugin::get_serialized_key_max_size(
    TypePluginDefaultEndpointData^ endpoint_data,
    Boolean include_encapsulation,
    UInt16 encapsulation_id,
    UInt32 current_alignment)
{

    UInt32 encapsulation_size = current_alignment;

    UInt32 initial_alignment = current_alignment;

    if (include_encapsulation) {
        if (!CdrStream::valid_encapsulation_id(encapsulation_id)) {
            return 1;
        }

        encapsulation_size = CdrSizes::ENCAPSULATION->serialized_size(
            current_alignment);
        current_alignment = 0;
        initial_alignment = 0;

    }

    current_alignment +=get_serialized_sample_max_size(
        endpoint_data,false, encapsulation_id, current_alignment);

    if (include_encapsulation) {
        current_alignment += encapsulation_size;
    }

    return current_alignment - initial_alignment;

}

/* ------------------------------------------------------------------------
Key Management functions:
* ------------------------------------------------------------------------ */

Boolean 
PositionPlugin::serialize_key(
    TypePluginDefaultEndpointData^ endpoint_data,
    Position^ sample,
    CdrStream% stream,    
    Boolean serialize_encapsulation,
    UInt16 encapsulation_id,
    Boolean serialize_key,
    Object^ endpoint_plugin_qos)
{

    CdrStreamPosition position;

    if (serialize_encapsulation) {
        /* Encapsulate sample */

        if (!stream.serialize_and_set_cdr_encapsulation(encapsulation_id)) {
            return false;
        }

        position = stream.reset_alignment();

    }

    if (serialize_key) {
        if (!serialize(
            endpoint_data,
            sample,
            stream,
            serialize_encapsulation,
            encapsulation_id, 
            serialize_key,
            endpoint_plugin_qos)) {
            return false;
        }

    }

    if(serialize_encapsulation) {
        stream.restore_alignment(position);
    }

    return true;

}

Boolean PositionPlugin::deserialize_key_sample(
    TypePluginDefaultEndpointData^ endpoint_data,
    Position^ sample,
    CdrStream% stream, 
    Boolean deserialize_encapsulation,
    Boolean deserialize_key,
    Object^ endpoint_plugin_qos)
{

    CdrStreamPosition position;

    if (deserialize_encapsulation) {
        /* Deserialize encapsulation */
        if (!stream.deserialize_and_set_cdr_encapsulation()) {
            return false;  
        }

        position = stream.reset_alignment();

    }

    if (deserialize_key) {

        if (!deserialize_sample(
            endpoint_data, sample, stream,
            deserialize_encapsulation,
            deserialize_key,
            endpoint_plugin_qos)) {
            return false;
        }

    }

    if(deserialize_encapsulation) {
        stream.restore_alignment(position);
    }

    return true;

}

Boolean
PositionPlugin::serialized_sample_to_key(
    TypePluginDefaultEndpointData^ endpoint_data,
    Position^ sample,
    CdrStream% stream, 
    Boolean deserialize_encapsulation,  
    Boolean deserialize_key, 
    Object^ endpoint_plugin_qos)
{

    CdrStreamPosition position;

    if(deserialize_encapsulation) {
        if (!stream.deserialize_and_set_cdr_encapsulation()) {
            return false;
        }

        position = stream.reset_alignment();

    }

    if (deserialize_key) {

        if (!deserialize_sample(
            endpoint_data,
            sample,
            stream,
            deserialize_encapsulation,
            deserialize_key,
            endpoint_plugin_qos)) {
            return false;
        }

    }

    if(deserialize_encapsulation) {
        stream.restore_alignment(position);
    }

    return true;

}

/* ------------------------------------------------------------------------
* Plug-in Lifecycle Methods
* ------------------------------------------------------------------------ */

PositionPlugin^
PositionPlugin::get_instance() {
    if (_singleton == nullptr) {
        _singleton = gcnew PositionPlugin();
    }
    return _singleton;
}

void
PositionPlugin::dispose() {
    delete _singleton;
    _singleton = nullptr;
}

