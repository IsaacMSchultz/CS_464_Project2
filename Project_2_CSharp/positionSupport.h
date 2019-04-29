/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from position.idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

#pragma once

#include "position.h"

class DDSDataWriter;
class DDSDataReader;

// ---------------------------------------------------------------------------
// PositionTypeSupport
// ---------------------------------------------------------------------------

ref class PositionPlugin;

/* A collection of useful methods for dealing with objects of type
* Position.
*/
public ref class PositionTypeSupport
: public DDS::TypedTypeSupport<Position^> {
    // --- Type name: --------------------------------------------------------
  public:
    static System::String^ TYPENAME = "Position";

    // --- Public Methods: ---------------------------------------------------
  public:
    /* Get the default name of this type.
    *
    * An application can choose to register a type under any name, so
    * calling this method is strictly optional.
    */
    static System::String^ get_type_name();

    /* Register this type with the given participant under the given logical
    * name. This type must be registered before a Topic can be created that
    * uses it.
    */
    static void register_type(
        DDS::DomainParticipant^ participant,
        System::String^ type_name);

    /* Unregister this type from the given participant, where it was
    * previously registered under the given name. No further Topic creation
    * using this type will be possible.
    *
    * Unregistration allows some middleware resources to be reclaimed.
    */
    static void unregister_type(
        DDS::DomainParticipant^ participant,
        System::String^ type_name);

    /* Create an instance of the Position type.
    */
    static Position^ create_data();

    /* If instances of the Position type require any
    * explicit finalization, perform it now on the given sample.
    */
    static void delete_data(Position^ data);

    /* Write the contents of the data sample to standard out.
    */
    static void print_data(Position^ a_data);

    /* Perform a deep copy of the contents of one data sample over those of
    * another, overwriting it.
    */
    static void copy_data(
        Position^ dst_data,
        Position^ src_data);

    static void serialize_data_to_cdr_buffer(
        array<System::Byte>^ buffer,
        System::UInt32% length,
        Position^ a_data);

    static void deserialize_data_from_cdr_buffer(
        Position^ a_data,
        array<System::Byte>^ buffer,
        System::UInt32 length);

    static System::String^ data_to_string(
        Position ^sample,
        PrintFormatProperty ^property);

    static System::String^ data_to_string(
        Position ^sample);

    static DDS::TypeCode^ get_typecode();

    // --- Implementation: ---------------------------------------------------
    /* The following code is for the use of the middleware infrastructure.
    * Applications are not expected to call it directly.
    */
  public:
    virtual System::String^ get_type_name_untyped() override;
    virtual DDS::DataReader^ create_datareaderI(
        System::IntPtr impl) override;
    virtual DDS::DataWriter^ create_datawriterI(
        System::IntPtr impl) override;

    virtual Position^ create_data_untyped() override;

  public:
    static PositionTypeSupport^ get_instance();

    PositionTypeSupport();

  private:
    static PositionTypeSupport^ _singleton;
    PositionPlugin^ _type_plugin;
};

// ---------------------------------------------------------------------------
// PositionDataReader
// ---------------------------------------------------------------------------

/**
* A reader for the Position type.
*/
public ref class PositionDataReader :
public DDS::TypedDataReader<Position^> {
    /* The following code is for the use of the middleware infrastructure.
    * Applications are not expected to call it directly.
    */
    internal:
    PositionDataReader(System::IntPtr impl);
};

// ---------------------------------------------------------------------------
// PositionDataWriter
// ---------------------------------------------------------------------------

/**
* A writer for the Position user type.
*/
public ref class PositionDataWriter :
public DDS::TypedDataWriter<Position^> {
    /* The following code is for the use of the middleware infrastructure.
    * Applications are not expected to call it directly.
    */
    internal:
    PositionDataWriter(System::IntPtr impl);
};
