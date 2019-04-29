/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from accident.idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

#include "accidentSupport.h"
#include "accidentPlugin.h"

#pragma unmanaged
#include "ndds/ndds_cpp.h"
#pragma managed

using namespace System;
using namespace DDS;

/* ========================================================================= */

// ---------------------------------------------------------------------------
// AccidentDataWriter
// ---------------------------------------------------------------------------

AccidentDataWriter::AccidentDataWriter(
    System::IntPtr impl) : DDS::TypedDataWriter<Accident^>(impl) {
    // empty
}

// ---------------------------------------------------------------------------
// AccidentDataReader
// ---------------------------------------------------------------------------

AccidentDataReader::AccidentDataReader(
    System::IntPtr impl) : DDS::TypedDataReader<Accident^>(impl) {
    // empty
}

// ---------------------------------------------------------------------------
// AccidentTypeSupport
// ---------------------------------------------------------------------------

AccidentTypeSupport::AccidentTypeSupport()
: DDS::TypedTypeSupport<Accident^>(
    AccidentPlugin::get_instance()) {

    _type_plugin = AccidentPlugin::get_instance();
}

void AccidentTypeSupport::register_type(
    DDS::DomainParticipant^ participant,
    System::String^ type_name) {

    get_instance()->register_type_untyped(participant, type_name);
}

void AccidentTypeSupport::unregister_type(
    DDS::DomainParticipant^ participant,
    System::String^ type_name) {

    get_instance()->unregister_type_untyped(participant, type_name);
}

Accident^ AccidentTypeSupport::create_data() {
    return gcnew Accident();
}

Accident^ AccidentTypeSupport::create_data_untyped() {
    return create_data();
}

void AccidentTypeSupport::delete_data(
    Accident^ a_data) {
    /* If the generated type does not implement IDisposable (the default),
    * the following will no a no-op.
    */
    delete a_data;
}

void AccidentTypeSupport::print_data(Accident^ a_data) {
    get_instance()->_type_plugin->print_data(a_data, nullptr, 0);
}

void AccidentTypeSupport::copy_data(
    Accident^ dst, Accident^ src) {

    get_instance()->copy_data_untyped(dst, src);
}

void AccidentTypeSupport::serialize_data_to_cdr_buffer(
    array<System::Byte>^ buffer,
    System::UInt32% length,
    Accident^ a_data)
{
    if (!get_instance()->_type_plugin->serialize_to_cdr_buffer(buffer,length,a_data)) {
        throw gcnew Retcode_Error(DDS_RETCODE_ERROR);
    }
}

void AccidentTypeSupport::deserialize_data_from_cdr_buffer(
    Accident^ a_data,
    array<System::Byte>^ buffer,
    System::UInt32 length)
{
    if (!get_instance()->_type_plugin->deserialize_from_cdr_buffer(a_data,buffer,length)) {
        throw gcnew Retcode_Error(DDS_RETCODE_ERROR);
    }
}

System::String^ AccidentTypeSupport::data_to_string(
    Accident ^sample, 
    PrintFormatProperty ^formatProperty)
{
    return get_instance()->_type_plugin->data_to_string(
        sample, 
        formatProperty);
}

System::String^ AccidentTypeSupport::data_to_string(
    Accident ^sample)
{
    PrintFormatProperty ^formatProperty = gcnew PrintFormatProperty();
    return get_instance()->_type_plugin->data_to_string(
        sample, 
        formatProperty);
}

DDS::TypeCode^ AccidentTypeSupport::get_typecode() {
    return  Accident::get_typecode();
}

System::String^ AccidentTypeSupport::get_type_name() {
    return TYPENAME;
}

System::String^ AccidentTypeSupport::get_type_name_untyped() {
    return TYPENAME;
}

DDS::DataReader^ AccidentTypeSupport::create_datareaderI(
    System::IntPtr impl) {

    return gcnew AccidentDataReader(impl);
}

DDS::DataWriter^ AccidentTypeSupport::create_datawriterI(
    System::IntPtr impl) {

    return gcnew AccidentDataWriter(impl);
}

AccidentTypeSupport^
AccidentTypeSupport::get_instance() {
    if (_singleton == nullptr) {
        _singleton = gcnew AccidentTypeSupport();
    }
    return _singleton;
}
