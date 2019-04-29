/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from position.idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

#include "positionSupport.h"
#include "positionPlugin.h"

#pragma unmanaged
#include "ndds/ndds_cpp.h"
#pragma managed

using namespace System;
using namespace DDS;

/* ========================================================================= */

// ---------------------------------------------------------------------------
// PositionDataWriter
// ---------------------------------------------------------------------------

PositionDataWriter::PositionDataWriter(
    System::IntPtr impl) : DDS::TypedDataWriter<Position^>(impl) {
    // empty
}

// ---------------------------------------------------------------------------
// PositionDataReader
// ---------------------------------------------------------------------------

PositionDataReader::PositionDataReader(
    System::IntPtr impl) : DDS::TypedDataReader<Position^>(impl) {
    // empty
}

// ---------------------------------------------------------------------------
// PositionTypeSupport
// ---------------------------------------------------------------------------

PositionTypeSupport::PositionTypeSupport()
: DDS::TypedTypeSupport<Position^>(
    PositionPlugin::get_instance()) {

    _type_plugin = PositionPlugin::get_instance();
}

void PositionTypeSupport::register_type(
    DDS::DomainParticipant^ participant,
    System::String^ type_name) {

    get_instance()->register_type_untyped(participant, type_name);
}

void PositionTypeSupport::unregister_type(
    DDS::DomainParticipant^ participant,
    System::String^ type_name) {

    get_instance()->unregister_type_untyped(participant, type_name);
}

Position^ PositionTypeSupport::create_data() {
    return gcnew Position();
}

Position^ PositionTypeSupport::create_data_untyped() {
    return create_data();
}

void PositionTypeSupport::delete_data(
    Position^ a_data) {
    /* If the generated type does not implement IDisposable (the default),
    * the following will no a no-op.
    */
    delete a_data;
}

void PositionTypeSupport::print_data(Position^ a_data) {
    get_instance()->_type_plugin->print_data(a_data, nullptr, 0);
}

void PositionTypeSupport::copy_data(
    Position^ dst, Position^ src) {

    get_instance()->copy_data_untyped(dst, src);
}

void PositionTypeSupport::serialize_data_to_cdr_buffer(
    array<System::Byte>^ buffer,
    System::UInt32% length,
    Position^ a_data)
{
    if (!get_instance()->_type_plugin->serialize_to_cdr_buffer(buffer,length,a_data)) {
        throw gcnew Retcode_Error(DDS_RETCODE_ERROR);
    }
}

void PositionTypeSupport::deserialize_data_from_cdr_buffer(
    Position^ a_data,
    array<System::Byte>^ buffer,
    System::UInt32 length)
{
    if (!get_instance()->_type_plugin->deserialize_from_cdr_buffer(a_data,buffer,length)) {
        throw gcnew Retcode_Error(DDS_RETCODE_ERROR);
    }
}

System::String^ PositionTypeSupport::data_to_string(
    Position ^sample, 
    PrintFormatProperty ^formatProperty)
{
    return get_instance()->_type_plugin->data_to_string(
        sample, 
        formatProperty);
}

System::String^ PositionTypeSupport::data_to_string(
    Position ^sample)
{
    PrintFormatProperty ^formatProperty = gcnew PrintFormatProperty();
    return get_instance()->_type_plugin->data_to_string(
        sample, 
        formatProperty);
}

DDS::TypeCode^ PositionTypeSupport::get_typecode() {
    return  Position::get_typecode();
}

System::String^ PositionTypeSupport::get_type_name() {
    return TYPENAME;
}

System::String^ PositionTypeSupport::get_type_name_untyped() {
    return TYPENAME;
}

DDS::DataReader^ PositionTypeSupport::create_datareaderI(
    System::IntPtr impl) {

    return gcnew PositionDataReader(impl);
}

DDS::DataWriter^ PositionTypeSupport::create_datawriterI(
    System::IntPtr impl) {

    return gcnew PositionDataWriter(impl);
}

PositionTypeSupport^
PositionTypeSupport::get_instance() {
    if (_singleton == nullptr) {
        _singleton = gcnew PositionTypeSupport();
    }
    return _singleton;
}
