package com.bzsoft.harrison.proto.stream.kryo;

import static com.esotericsoftware.kryo.util.Util.className;
import static com.esotericsoftware.kryo.util.Util.getWrapperClass;
import static com.esotericsoftware.kryo.util.Util.log;
import static com.esotericsoftware.minlog.Log.DEBUG;
import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

import java.lang.reflect.InvocationHandler;
import java.net.URI;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.regex.Pattern;

import com.esotericsoftware.kryo.ClassResolver;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptyListSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptyMapSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptySetSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsSingletonListSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsSingletonMapSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsSingletonSetSerializer;
import com.esotericsoftware.kryo.util.IdentityObjectIntMap;
import com.esotericsoftware.kryo.util.IntMap;
import com.esotericsoftware.kryo.util.MapReferenceResolver;
import com.esotericsoftware.kryo.util.ObjectMap;

import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.BitSetSerializer;
import de.javakaffee.kryoserializers.DateSerializer;
import de.javakaffee.kryoserializers.EnumMapSerializer;
import de.javakaffee.kryoserializers.EnumSetSerializer;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.RegexSerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.URISerializer;
import de.javakaffee.kryoserializers.UUIDSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;

public class AutoKryo extends Kryo {

	@SuppressWarnings("rawtypes")
	private static final class AutoClassResolver implements ClassResolver {

		private static final byte								NAME	= -1;

		protected Kryo												kryo;
		protected final IntMap<Registration>				idToRegistration;
		protected final ObjectMap<Class, Registration>	classToRegistration;

		protected IdentityObjectIntMap<Class>				classToNameId;
		protected IntMap<Class>									nameIdToClass;
		protected ObjectMap<String, Class>					nameToClass;
		protected int												nextNameId;

		private AutoClassResolver() {
			kryo = null;
			idToRegistration = new IntMap<Registration>();
			classToRegistration = new ObjectMap<Class, Registration>();
		}

		@Override
		public void setKryo(final Kryo kryo) {
			this.kryo = kryo;
		}

		@Override
		public Registration register(final Registration registration) {
			if (registration == null) {
				throw new IllegalArgumentException("registration cannot be null.");
			}
			if (registration.getId() != NAME) {
				if (TRACE) {
					trace("kryo", "Register class ID " + registration.getId() + ": " + className(registration.getType()) + " ("
							+ registration.getSerializer().getClass().getName() + ")");
				}
				idToRegistration.put(registration.getId(), registration);
			} else if (TRACE) {
				trace("kryo", "Register class name: " + className(registration.getType()) + " (" + registration.getSerializer().getClass().getName()
						+ ")");
			}
			classToRegistration.put(registration.getType(), registration);
			if (registration.getType().isPrimitive()) {
				classToRegistration.put(getWrapperClass(registration.getType()), registration);
			}
			return registration;
		}

		@Override
		public Registration registerImplicit(final Class type) {
			return register(new Registration(type, kryo.getDefaultSerializer(type), NAME));
		}

		@Override
		public Registration getRegistration(final int classID) {
			return idToRegistration.get(classID);
		}

		@Override
		public Registration getRegistration(final Class type) {
			final Registration registration = classToRegistration.get(type);
			return registration;
		}

		@Override
		public Registration writeClass(final Output output, final Class type) {
			if (type == null) {
				if (TRACE || DEBUG && kryo.getDepth() == 1) {
					log("Write", null);
				}
				output.writeVarInt(Kryo.NULL, true);
				return null;
			}
			//			if (!type.isAssignableFrom(Serializable.class)){
			//				throw new ProtocolRuntimeException(type+" is not Serializable");
			//			}
			final Registration registration = kryo.getRegistration(type);
			if (registration.getId() == NAME) {
				writeName(output, type, registration);
			} else {
				if (TRACE) {
					trace("kryo", "Write class " + registration.getId() + ": " + className(type));
				}
				output.writeVarInt(registration.getId() + 2, true);
			}
			return registration;
		}

		protected void writeName(final Output output, final Class type, final Registration registration) {
			output.writeVarInt(NAME + 2, true);
			if (classToNameId != null) {
				final int nameId = classToNameId.get(type, -1);
				if (nameId != -1) {
					if (TRACE) {
						trace("kryo", "Write class name reference " + nameId + ": " + className(type));
					}
					output.writeVarInt(nameId, true);
					return;
				}
			}
			// Only write the class name the first time encountered in object graph.
			if (TRACE) {
				trace("kryo", "Write class name: " + className(type));
			}
			final int nameId = nextNameId++;
			if (classToNameId == null) {
				classToNameId = new IdentityObjectIntMap<Class>();
			}
			classToNameId.put(type, nameId);
			output.writeVarInt(nameId, true);
			output.writeString(type.getName());
		}

		@Override
		public Registration readClass(final Input input) {
			final int classID = input.readVarInt(true);
			switch (classID) {
			case Kryo.NULL:
				if (TRACE || DEBUG && kryo.getDepth() == 1) {
					log("Read", null);
				}
				return null;
			case NAME + 2: // Offset for NAME and NULL.
				return readName(input);
			}
			final Registration registration = idToRegistration.get(classID - 2);
			if (registration == null) {
				throw new KryoException("Encountered unregistered class ID: " + (classID - 2));
			}
			if (TRACE) {
				trace("kryo", "Read class " + (classID - 2) + ": " + className(registration.getType()));
			}
			return registration;
		}

		protected Registration readName(final Input input) {
			final int nameId = input.readVarInt(true);
			if (nameIdToClass == null) {
				nameIdToClass = new IntMap<Class>();
			}
			Class type = nameIdToClass.get(nameId);
			if (type == null) {
				// Only read the class name the first time encountered in object
				// graph.
				final String className = input.readString();
				type = getTypeByName(className);
				if (type == null) {
					try {
						type = Class.forName(className, false, kryo.getClassLoader());
					} catch (final ClassNotFoundException ex) {
						throw new KryoException("Unable to find class: " + className, ex);
					}
					if (nameToClass == null) {
						nameToClass = new ObjectMap<String, Class>();
					}
					nameToClass.put(className, type);
				}
				nameIdToClass.put(nameId, type);
				if (TRACE) {
					trace("kryo", "Read class name: " + className);
				}
			} else {
				if (TRACE) {
					trace("kryo", "Read class name reference " + nameId + ": " + className(type));
				}
			}
			return kryo.getRegistration(type);
		}

		protected Class<?> getTypeByName(final String className) {
			return nameToClass != null ? nameToClass.get(className) : null;
		}

		@Override
		public void reset() {
			//empty
		}
	}

	public AutoKryo() {
		super(new AutoClassResolver(), new MapReferenceResolver());
		register( Arrays.asList( "" ).getClass(), new ArraysAsListSerializer() );
		register( Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer() );
		register( Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer() );
		register( Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer() );
		register( Collections.singletonList( "" ).getClass(), new CollectionsSingletonListSerializer() );
		register( Collections.singleton( "" ).getClass(), new CollectionsSingletonSetSerializer() );
		register( Collections.singletonMap( "", "" ).getClass(), new CollectionsSingletonMapSerializer() );
		register( Date.class, new DateSerializer(Date.class));
		register( java.sql.Date.class, new DateSerializer(java.sql.Date.class));
		register( java.sql.Time.class, new DateSerializer(java.sql.Time.class));
		register( java.sql.Timestamp.class, new DateSerializer(java.sql.Timestamp.class));
		register( GregorianCalendar.class, new GregorianCalendarSerializer() );
		register( BitSet.class, new BitSetSerializer());
		register( Pattern.class, new RegexSerializer());
		register( URI.class, new URISerializer());
		register( UUID.class, new UUIDSerializer());
		register( EnumMap.class, new EnumMapSerializer());
		register( EnumSet.class, new EnumSetSerializer());
		register( InvocationHandler.class, new JdkProxySerializer() );
		UnmodifiableCollectionsSerializer.registerSerializers( this );
		SynchronizedCollectionsSerializer.registerSerializers( this );
	}

}
